"""
UI Layer: input/output only, no logic 

* Display menus
* Collects user input
* Calls service methods 

"""
from service.user_service import login
from service.expense_service import submit_new_expense, get_expenses_dao, edit_expense, delete_expense, get_my_expenses, get_expense_history
from tabulate import tabulate # for the table
from getpass import getpass # for the password encryption (user)

def login_menu():
    attempts = 0
    while attempts < 3:
        username = input("Username: ")
        password = getpass("Password: ")
        login_attempt = login(username, password)
        
        if login_attempt is not None:
            print("\nWelcome to the main employee menu")
            employee_menu(login_attempt)
            break
        else:
            print("Invalid credientials") 
            attempts += 1
    else:
        print("Too many failed attempts. Goodbye.")
    
def employee_menu(user):
    while True:
        print("\n--- Employee Menu ---")
        print("1. Submit new expense")
        print("2. View pending expenses")
        print("3. Edit expense")
        print("4. Delete expense")
        print("5. View history")
        print("6. Logout")
        
        choice = input("Enter your choice: ")
        
        if choice == "1":
            submit_expense_menu(user)
        elif choice == "2":
            view_expenses_menu(user)
        elif choice == "3":
            edit_expense_menu(user)
        elif choice == "4":
            delete_expense_menu(user)
        elif choice == "5":
            view_history_menu(user)
        elif choice == "6":
            print("\nLogging out...")
            break
        else:
            print("Invalid choice, please try again")


def submit_expense_menu(user):
    # Reprompt the user until they put in a valid amount and description
    while True:
        amount = input("Enter Amount: ")
        try:
            if float(amount) <= 0:
                print("Amount must be greater than 0. Please try again.")
                continue
            break
        except ValueError:
            print("Invalid amount. Please enter a number.")
            continue

    while True:
        description = input("Enter Description: ")
        if not description or not description.strip():
            print("Description cannot be empty. Please try again.")
            continue
        break

    category = input("Enter Category (optional): ")

    result = submit_new_expense(user[0], amount, description, category)

    if result is True:
        print("Successfully submitted an expense!")
    elif result is False:
        print("Could not submit expense. Please check your input.")
    else:
        print("An error occurred while submitting the expense.")

        
def view_expenses_menu(user):
    expenses = get_my_expenses(user[0], 'pending')
    
    if not expenses:
        print("No expenses found")
        return

    title = "My Expenses"
    print("\n--------------------------- My Expenses -------------------------------")
    # each tuple's values map to the headers by position.
    headers = ["ID", "Amount", "Description", "Date", "Status", "Category"]
    print(tabulate(expenses, headers=headers, tablefmt="rounded_outline"))

        
    
def edit_expense_menu(user):
    # show their pending expenses first so they know what to pick
    expenses = get_my_expenses(user[0], 'pending')
    
    if not expenses:
        print("No expenses found")
        return

    print("\n--------------------------- My Expenses -------------------------------")
    headers = ["ID", "Amount", "Description", "Date", "Status", "Category"]
    print(tabulate(expenses, headers=headers, tablefmt="rounded_outline"))

    while True:
        expense_id = input("Enter the expense ID to edit: ")

        # check if its a valid number
        if not expense_id.isdigit():
            print("Invalid input. Please enter a numeric ID.")
            continue

        # then check if its in pending expenses
        valid_ids = [str(expense[0]) for expense in expenses]
        if expense_id not in valid_ids:
            print("Expense not found. Please choose an ID from the list above.")
            continue

        break

    while True:
        new_amount = input("Enter new amount: ")
        try:
            if float(new_amount) <= 0:
                print("Amount must be greater than 0. Please try again.")
                continue
            break
        except ValueError:
            print("Invalid amount. Please enter a number.")
            continue
    while True:
        new_description = input("Enter new description (press Enter to keep current): ")
        if not new_description.strip():
            # if they didnt provide anything use the current description
            current_expense = next(e for e in expenses if str(e[0]) == expense_id)
            new_description = current_expense[2] # description is index 2
            break
        break

    result = edit_expense(expense_id, user[0], new_amount, new_description)
    
    if result is True:
        print("Expense updated successfully!")
    elif result is False:
        print("Could not update - this expense may not belong to you, or is no longer pending.")
    else:
        print("Invalid amount entered.")
    
    
    
def delete_expense_menu(user):
    # show their pending expenses first so they know what to pick
    expenses = get_my_expenses(user[0], 'pending')
    
    if not expenses:
        print("No expenses found")
        return

    print("\n--------------------------- My Expenses -------------------------------")
    headers = ["ID", "Amount", "Description", "Date", "Status", "Category"]
    print(tabulate(expenses, headers=headers, tablefmt="rounded_outline"))
    
    expense_id = input("Enter the expense ID to delete: ")
    
    if expense_id.isdigit():
        result = delete_expense(user[0], expense_id)
        if result is True:
            print("Expense deleted successfully!")
        elif result is False:
            print("Could not delete - this expense may not belong to you, or is no longer pending.")
        else:
            print("An error occurred while deleting the expense.")
    else: 
        print("Invalid input. Please enter a numeric expense ID.")
        

# I think this works but i have to go on the manager side to test
def view_history_menu(user):
    expenses = get_expense_history(user[0])
    
    if not expenses:
        print("No expenses found")
        return

    print("\n--------------------------- My Expenses -------------------------------")
    headers = ["ID", "Amount", "Description", "Date", "Status", "Category"]
    print(tabulate(expenses, headers=headers, tablefmt="rounded_outline"))