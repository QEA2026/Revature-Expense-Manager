import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    filename="expense_manager.log",
    filemode="a"
)
def get_logger(name):
    return logging.getLogger(name)
