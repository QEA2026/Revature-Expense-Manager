"""
dao layer: only talks to the database (no logic)

Insert, Select, update, and delete expenses

"""
from db.connection import get_connection
import datetime
from logger import get_logger
logger = get_logger(__name__)

def submit_new_expense_dao(user_id, amount, description, category):
    conn = get_connection()
    try:
        cur = conn.cursor()
        date = str(datetime.date.today())
        # everytime you submit a new expense you also have to need it to get approved
        cur.execute(" INSERT INTO expenses (user_id, amount, description, date, category) values (?,?,?,?,?)",
                    (user_id, amount, description, date, category))
        cur.execute(" INSERT INTO approvals (expense_id, status) values (?,?)",
                    (cur.lastrowid, 'pending'))

        expense_id = cur.lastrowid
        conn.commit()
        logger.info(f"Successfully added expense {expense_id} for user_id: {user_id}")
        return True
    except Exception as e:
        logger.error(f"Error submitting expense: {e}")
        return None
    finally:
        conn.close()

# view the status of ALL of my expenses given a user_id
def get_expenses_dao(user_id):
    conn = get_connection()
    try:
        cur = conn.cursor()
        cur.execute("""
            SELECT expenses.id, 
                   expenses.amount, 
                   expenses.description, 
                   expenses.date, 
                   approvals.status,
                   expenses.category
            FROM expenses
            JOIN approvals ON approvals.expense_id = expenses.id
            WHERE expenses.user_id = ?
        """, (user_id,))
        
        result = cur.fetchall()
        return result
    except Exception as e:
        logger.error(f"Error retrieving expenses: {e}")
        return None
    finally:
        conn.close()
        
def get_expense_by_status(user_id, status):
    conn = get_connection()
    try:
        cur = conn.cursor()
        cur.execute("""
            SELECT expenses.id, 
                   expenses.amount, 
                   expenses.description, 
                   expenses.date, 
                   approvals.status,
                   expenses.category
            FROM expenses
            JOIN approvals ON approvals.expense_id = expenses.id
            WHERE expenses.user_id = ?
            AND approvals.status = ?
        """, (user_id,status))
        
        result = cur.fetchall()
        return result
    except Exception as e:
        logger.error(f"Error retrieving {status} expenses: {e}")
        return None
    finally:
        conn.close()

# get all expenses (that have been approved or denied)
def get_expense_history_dao(user_id):
    conn = get_connection()
    try:
        cur = conn.cursor()
        cur.execute("""
            SELECT expenses.id, 
                   expenses.amount, 
                   expenses.description, 
                   expenses.date,
                   approvals.status,
                   expenses.category
            FROM expenses
            JOIN approvals ON approvals.expense_id = expenses.id
            WHERE expenses.user_id = ?
            AND approvals.status IN ('approved', 'denied')
        """, (user_id,))
        
        result = cur.fetchall()
        return result
    except Exception as e:
        logger.error(f"Error retrieving expenses: {e}")
        return None
    finally:
        conn.close()
    
    


def edit_expense_dao(expense_id, user_id, new_amount, new_description):
    conn = get_connection()
    try:
        cur = conn.cursor()
        
        # Check if expense exists and is still pending
        cur.execute("""
            SELECT approvals.status
            FROM approvals
            JOIN expenses ON approvals.expense_id = expenses.id
            WHERE expenses.id = ?
            AND expenses.user_id = ?
        """, (expense_id, user_id))
        
        result = cur.fetchone()
        
        # If no expense found or not pending, return false
        if result is None:
            logger.warning(f"Expense {expense_id} not found")
            return False
        if result[0] != 'pending':
            logger.warning(f"Expense {expense_id} for user {user_id} is not pending - cannot edit")
            return False
        
        
        # Run the update
        cur.execute("""
            UPDATE expenses
            SET amount = ?, description = ?
            WHERE id = ?
            AND user_id = ?
        """, (new_amount, new_description, expense_id, user_id))
        
        conn.commit()
        logger.info(f"Updated expense {expense_id}")
        return True
    
    except Exception as e:
        logger.error(f"Error editing expense: {e}")
        return None
    finally:
        conn.close()
    
def delete_expense_dao(user_id, expense_id):
    conn = get_connection()
    try:
        cur = conn.cursor()
        
        # Check if expense exists and is still pending
        cur.execute("""
            SELECT approvals.status
            FROM approvals
            JOIN expenses ON approvals.expense_id = expenses.id
            WHERE expenses.id = ?
            AND expenses.user_id = ?
        """, (expense_id, user_id))
        
        result = cur.fetchone()
        
        # If no expense found or not pending, return false
        if result is None:
            logger.warning(f"Expense {expense_id} not found")
            return False
        if result[0] != 'pending':
            logger.warning(f"Expense {expense_id} for user {user_id} is not pending - cannot delete")
            return False
        
        # Delete the approval record first (dependent table)
        cur.execute("DELETE from approvals WHERE expense_id = ?", (expense_id,))
        
        # Then the expense itself
        cur.execute("DELETE FROM expenses WHERE user_id = ? AND id = ?", (user_id, expense_id))
        
        conn.commit()
        logger.info(f"Deleted expense {expense_id}")
        return True
    except Exception as e:
        logger.error(f"Error deleting expense: {e}")
        return None
    finally: 
        conn.close()