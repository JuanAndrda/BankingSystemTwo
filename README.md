# Banking System - OOP Project

A comprehensive command-line banking application demonstrating Object-Oriented Programming principles and Data Structures.

## 🚀 Quick Start

```bash
# Compile
javac -d out -sourcepath src src/com/banking/Main.java

# Run
java -cp out com.banking.Main
```

**Login:** `admin` / `admin123` (or see [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) for more credentials)

## 📚 Documentation

This project includes comprehensive documentation files:

| File | Description |
|------|-------------|
| **[PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)** | Complete project summary, features, and how to run |
| **[OOP_CONCEPTS.md](OOP_CONCEPTS.md)** | Detailed explanation of all OOP principles used |
| **[CLASS_RELATIONSHIPS.md](CLASS_RELATIONSHIPS.md)** | Class diagrams and relationship explanations |
| **[DATA_STRUCTURES.md](DATA_STRUCTURES.md)** | Data structures and algorithms explained |
| **[TRANSACTION_LOGIC_EXPLAINED.md](TRANSACTION_LOGIC_EXPLAINED.md)** | How deposit, withdraw, and transfer work |

## ✨ Features

- ✅ Customer Management (CRUD)
- ✅ Account Management (Savings & Checking)
- ✅ Transaction Processing (Deposit, Withdraw, Transfer)
- ✅ User Authentication (Admin & Customer roles)
- ✅ Audit Logging
- ✅ Sorting Algorithms (Insertion Sort)
- ✅ Data Structures (LinkedList, Stack)

## 🎯 OOP Concepts Demonstrated

- **Encapsulation** - Private fields with getters/setters
- **Inheritance** - Account hierarchy (Savings, Checking)
- **Polymorphism** - Different withdraw behaviors
- **Abstraction** - Abstract Account and User classes

## 📊 Project Stats

- **Total Files:** 22 Java files
- **Total Lines:** ~4,058 lines (simplified from ~6,000+)
- **Packages:** 4 (models, managers, auth, utilities)
- **Documentation:** 5 comprehensive MD files

## 🏗️ Project Structure

```
src/com/banking/
├── models/          # Business entities (Account, Customer, Transaction)
├── managers/        # Business logic (TransactionProcessor, AccountManager)
├── auth/            # Authentication (User, Admin, UserAccount)
└── utilities/       # Helpers (ValidationPatterns, InputValidator)
```

## 🎓 Learning Outcomes

This project demonstrates:
- All 4 OOP pillars (Encapsulation, Inheritance, Polymorphism, Abstraction)
- Data Structures (LinkedList, Stack)
- Sorting Algorithms (Insertion Sort)
- Clean Code principles
- Real-world application design

## 📖 Read the Docs!

Start with **[PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)** for a complete guide!

---

**Author:** Juan R.
**Course:** OOP + Data Structures
**Date:** November 2024
