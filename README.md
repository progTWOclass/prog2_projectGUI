# Personal Finance Tracker - JavaFX Application

## Overview

Personal Finance Tracker is a JavaFX application that helps user manage their personal finances. The application allows the user to track their incomes, expenses, and set a spending limit for different categories. It also provides some visual representation of their transactions and can allow the user to save/load their finances into a text file.

## Table of Contents

1. [Features]
2. [Usage]
3. [Project Structure]
4. [Challenges]

## Features

- add income and expenses transaction
- Categorize expenses (Food, Travel, Entertainment, etc.)
- Search a transactions by description
- Save/Load data to a text files
- edit/delete a transaction
- Set/delete spending limits for a category
- Display a pie chart of expenses
- Track expenses and spending limit with a bar chart

## Usage

### Adding Transactions

1. Click "Add Transaction" button
2. Enter a transaction description
3. Enter an amount
4. Select a date
5. Select transaction type (Income/Expense)
6. For expenses, select a category
7. Click "Submit"

### Editing a Transaction

1. Select a transaction from the table
2. CLick "Edit" button
3. modify the desired details
4. Click "Submit"

### Deleting a Transaction

1. Select a transaction from the table
2. CLick "Delete" button
3. confirm deletion

### Searching a Transaction

1. Enter a keyword in the search field
2. CLick "Search" button
3. Clear the search field
4. Click "Search" again to show all transactions

### Adding a Spending Limit

1. Click the "Add/Delete Spending Limit" button
2. Select an expense category
3. Enter the limit amount
4. Click "Submit" to set the limit

### Deleting a Spending Limit

1. Click the "Add/Delete Spending Limit" button
2. Select an expense category
3. Click "Delete" to set the limit
4. confirm deletion

### Editing a Spending Limit

1. Click the "Add/Delete Spending Limit" button
2. Select an expense category
3. Enter the limit amount
4. Click "Submit" to set the limit

### Saving and Loading Data

1. Click "Save to file" to save transactions to a file
2. Choose a location and filename
3. Click "Load File" to load previously saved transactions and spending limits
4. Choose transaction file and spending limit file to load

### Viewing Reports

- **Current Balance**: Shown in top-left corner of the table
- **show number of limits exceeded**: top of the Spending limit bar chart
- **Spending Bar Chart**: Compares expenses vs limits
- **Expense Pie Chart**: Shows expense breakdown by category

## Project Structure

### Classes

- Transaction: abstract class for financial transaction
- Income: subclass of Transaction for income records
- Expense: subclass of Transaction for expense records with categories
- SpendingLimit: Class representing spending limits for expense
- FinanceManger: Manage collections of transaction and spending limit

### Interface

- FileHandler: interface showing method signature
- HandleFileImplement: implementation of FileHandler, showing logic in method signatures

### Controller classes

- MainController: control the main application window
- AddController: control add/edit transaction window
- SpendLimController: control add/delete/edit spending limit window

### Custom Exception classes

- InvalidAmountException: Thrown when an invalid amount is entered
- SpendingLimitExceededException: Thrown when a transaction exceeds a spending limit
- TransactionNotFoundException: Thrown when a searched transaction is not found
- SpendingLimitNotFoundException: Thrown when a spending limit is not found

## Challenges

### Issues

1. **Spending limit Bar Chart not updating**

   - Visual Misrepresentation: Expenses and limits weren't aligned properly by category
   - Static Limits Appeared Dynamic: Limit bars changed height when they should remain fixed
   - Wrong Chart Type: Stacked bars made it hard to compare expenses vs limits. used bar chart instead

2. **errors**

   - User exceeds spending limit → warning appears
   - When clicking OK, another identical warning appears

3. **Spending limit warning showing unexpectedly**

   - Edit a transaction's description (no amount change)
   - Warning appears despite no limit impact

## Credits

Developed by: Steve Banh 1971537
For Programming 2 Section 02 – Winter 2025
Vanier College
