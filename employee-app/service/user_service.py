"""
Service layer: business logic only, no DB calls

* Does the password match?
Output should be: (1, "vanessa", "hashedpassword123", "employee")

"""
from dao.user_dao import find_user_by_username
from logger import get_logger
import bcrypt

logger = get_logger(__name__)

def login(username, password):
    try:
        user = find_user_by_username(username)
        
        if user is None:
            logger.warning(f"User with username {username} does not exist")
            return None

        if user[3] == "manager":
            logger.warning(f"Cannot login to employee side with manager credentials")
            return None
        
        if bcrypt.checkpw(password.encode(), user[2].encode()):
            logger.info(f"Successful login for user: {username}")
            return user
        else:
            logger.warning(f"Failed login attempt - incorrect password for user: {username}")
            return None
        
    except Exception as e:
        logger.error(f"Error during login for {username}: {e}")
        return None