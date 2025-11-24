package com.banking;

/**
 * MenuAction Enum - Represents user's exit choice from the banking menu
 *
 * Tracks what action the user chose when exiting the menu:
 * - LOGOUT: User chose to logout and return to login screen (another user can log in)
 * - EXIT_APPLICATION: User chose to quit the entire application
 *
 * Used by BankingSystem.startApplication() to determine whether to loop back
 * to login screen or terminate the application.
 */
public enum MenuAction {
    /**
     * User selected "Logout" - return to login screen for next user
     */
    LOGOUT,

    /**
     * User selected "Exit Application" - terminate the program
     */
    EXIT_APPLICATION;
}
