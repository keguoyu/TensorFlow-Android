from split import data_set_split
from train import train_start

if __name__ == '__main__':
    raw_data_dir = "./raw_dir"
    target_data_dir = "./target_dir"
    data_set_split(raw_data_dir, target_data_dir)

    train_start()
