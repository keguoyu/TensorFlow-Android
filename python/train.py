# 模型训练
from time import *

import tensorflow as tf
import matplotlib.pyplot as plt
from keras.src.layers import MaxPooling2D, Conv2D, Flatten, Dense
from keras.src.ops import dtype
from tensorflow.keras.layers import *

import ssl
import urllib.request
ssl._create_default_https_context = ssl._create_unverified_context
response = urllib.request.urlopen('https://example.com')

img_height = 224
img_width = 224
batch_size = 16

# 加载数据
def load_data(train_dir, test_dir):
    train_dataset = tf.keras.preprocessing.image_dataset_from_directory(
        train_dir,
        label_mode='categorical',
        seed=123,
        image_size=(img_height, img_width),
        batch_size=batch_size
    )

    test_dataset = tf.keras.preprocessing.image_dataset_from_directory(
        test_dir,
        label_mode='categorical',
        seed=123,
        image_size=(img_height, img_width),
        batch_size=batch_size
    )

    train_dataset1 = train_dataset.map(lambda x, y: (tf.cast(x, tf.float32), y))

    test_dataset1 = test_dataset.map(lambda x, y: (tf.cast(x, tf.float32), y))

    labels = train_dataset.class_names

    print(labels)

    return train_dataset1, test_dataset1, labels


# 创建训练模型
def load_model(labels_num):
    img_shape = (224, 224, 3)
    # 要将模型用到手机端
    base_model = tf.keras.applications.MobileNetV2(
        input_shape=img_shape,
        include_top=False,
        weights='imagenet'
    )
    base_model.trainable = False
    model = tf.keras.models.Sequential([
        tf.keras.layers.Input(shape=(224, 224, 3), dtype=tf.float32, name="input"),
        Rescaling(1. / 255, input_shape=img_shape),
        base_model,
        GlobalAveragePooling2D(),
        Dropout(0.5),  # 防止过拟合
        Dense(128, activation='relu'),
        Dense(labels_num, activation='softmax')
        # base_model,
        # # 对模型做归一化的处理，将0-255之间的数字统一处理到0到1之间
        # Rescaling(1. / 255, input_shape=img_shape),
        # # 卷积层，该卷积层的输出为32个通道，卷积核的大小是3*3，激活函数为relu
        # tf.keras.layers.Conv2D(32, (3, 3), activation='relu'),
        # # 添加池化层，池化的kernel大小是2*2
        # tf.keras.layers.MaxPooling2D(2, 2),
        # # Add another convolution
        # # 卷积层，输出为64个通道，卷积核大小为3*3，激活函数为relu
        # tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
        # # 池化层，最大池化，对2*2的区域进行池化操作
        # tf.keras.layers.MaxPooling2D(2, 2),
        # # 将二维的输出转化为一维
        # tf.keras.layers.Flatten(),
        # # The same 128 dense layers, and 10 output layers as in the pre-convolution example:
        # tf.keras.layers.Dense(128, activation='relu'),
        # # 通过softmax函数将模型输出为类名长度的神经元上，激活函数采用softmax对应概率值
        # tf.keras.layers.Dense(labels_num, activation='softmax')
    ])

    # 输出模型信息
    model.summary()
    # 指明模型的训练参数，优化器为sgd优化器，损失函数为交叉熵损失函数
    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )
    return model

def show_train_step(history):
    train_accuracy = history.history['accuracy']
    validation_accuracy = history.history['val_accuracy']

    train_loss= history.history['loss']
    validation_loss = history.history['val_loss']

    plt.figure(figsize=(8, 8))
    plt.subplot(2, 1, 1)
    plt.plot(train_accuracy, label='Training Accuracy')
    plt.plot(validation_accuracy, label='Validation Accuracy')
    plt.legend(loc='lower right')
    plt.ylabel('Accuracy')
    plt.ylim([min(plt.ylim()), 1])
    plt.title('Training and Validation Accuracy')

    plt.subplot(2, 1, 2)
    plt.plot(train_loss, label='Training Loss')
    plt.plot(validation_loss, label='Validation Loss')
    plt.legend(loc='upper right')
    plt.ylabel('Cross Entropy')
    plt.title('Training and Validation Loss')
    plt.xlabel('epoch')
    plt.savefig('results/results_cnn.png', dpi=100)

def train_start():
    start_time = time()
    (train_dataset, test_dataset, labels) = load_data(
        "./target_dir/train",
        "./target_dir/test"
    )
    print(labels)
    model = load_model(len(labels))
    history = model.fit(train_dataset, validation_data=test_dataset, epochs=20)
    model.export('animals_model')

    # 创建转换器并转换
    converter = tf.lite.TFLiteConverter.from_saved_model('animals_model')
    converter.optimizations = []  # 关键：禁用量化

    converter.optimizations = [tf.lite.Optimize.DEFAULT]  # 默认量化
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS,  # 默认支持的 TFLite 操作
        tf.lite.OpsSet.SELECT_TF_OPS  # 额外支持的非默认操作（如 Conv2D、MatMul）
    ]

    tflite_model = converter.convert()

    # 保存 TFLite 模型
    with open('model.tflite', 'wb') as f:
        f.write(tflite_model)
    #  f.write(tflite_model)
    end_time = time()
    cost_time = end_time - start_time
    print('该循环程序运行时间：', cost_time, "s")
    show_train_step(history)