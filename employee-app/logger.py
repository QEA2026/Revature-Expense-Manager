import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    datefmt='%d-%b-%y %H:%M:%S',
    filename="expense_manager.log",
    filemode="a"
)
def get_logger(name):
    return logging.getLogger(name)
