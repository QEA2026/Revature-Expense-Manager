class ExpenseManagerError(Exception):
    """Base application error."""


class AuthenticationError(ExpenseManagerError):
    """Raised when login details are invalid."""


class AuthorizationError(ExpenseManagerError):
    """Raised when a user is not allowed to access a feature."""


class ValidationError(ExpenseManagerError):
    """Raised when user input fails validation."""


class RecordNotFoundError(ExpenseManagerError):
    """Raised when the requested expense does not exist."""
