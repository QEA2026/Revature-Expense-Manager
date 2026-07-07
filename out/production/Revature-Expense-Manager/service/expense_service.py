"""
Service layer: business logic only, no DB calls

* is this expense still pending before allowing an edit?
* Does this expense belong to this employer?

"""

from dao.expense_dao import submit_new_expense_dao, get_expenses_dao, edit_expense_dao, delete_expense_dao, get_expense_by_status, get_expense_history_dao

# Validate the info given from the UI
def submit_new_expense(user_id, amount, description, category):
    try:
        if float(amount) <= 0:
            return False
        # if description is none or if its "  "
        if not description or not description.strip(): 
            return False
        submit_new_expense_dao(user_id, amount, description, category)
        return True
    except ValueError as e:
        print(f"Error couldn't convert {amount} to float: {e}")
        return None

# Returns all expenses for a user, or filtered by status if provided
def get_my_expenses(user_id, status = None):
    if status is None:
        return get_expenses_dao(user_id)
    else:
        return get_expense_by_status(user_id, status)



def edit_expense(expense_id, user_id, new_amount, new_description):
    try: 
        if float(new_amount) < 0 :
            return False 
        if not new_description or not new_description.strip(): 
            return False
        edit_expense_dao(expense_id, user_id, new_amount, new_description)
        return True
    except ValueError as e:
        print(f"Error couldn't convert {new_amount} to float: {e}")
        return None
    
# We already did the check in DAO to avoid a round trip 
def delete_expense(user_id, expense_id):
      return delete_expense_dao(user_id,expense_id)

def get_expense_history(user_id):
    return get_expense_history_dao(user_id)