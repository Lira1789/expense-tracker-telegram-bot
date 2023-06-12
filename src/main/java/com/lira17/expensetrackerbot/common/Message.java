package com.lira17.expensetrackerbot.common;

import com.vdurmont.emoji.EmojiParser;

public class Message {
    public static final String TOTAL_BALANCE_POSITIVE_MSG = EmojiParser.parseToUnicode(":white_check_mark:") + """
            <b>Good news!</b>
            Everything is going according!
            <b>Your total balance:</b> %s
            """;

    public static final String TOTAL_BALANCE_NEGATIVE_MSG = EmojiParser.parseToUnicode(":exclamation:") + """
            <b>Bad news!</b>
            Your expenses have surpassed your income!
            <b>Your total balance:</b> %s
            """;

    public static final String REPORT_POSITIVE_MSG = EmojiParser.parseToUnicode(":white_check_mark:") + """
            <b>Good news!</b>
            Everything is going according!
            Your report for %s:
            <b>Expenses:</b> %s
            <b>Incomes:</b> %s
            <b>Okay to spend:</b> %s
            """;

    public static final String REPORT_NEGATIVE_MSG = EmojiParser.parseToUnicode(":exclamation:") + """
            <b>Bad news!</b>
            Your expenses have surpassed your incomes!
            Your report for %s:
            <b>Expenses:</b> %s
            <b>Incomes:</b> %s
            <b>Overdraft:</b> %s
            """;

    public static final String EXPENSE_MSG = EmojiParser.parseToUnicode(":heavy_minus_sign:") + """
            <b>Expense</b>
            <b>Title:</b> %s
            <b>Description:</b> %s
            <b>Amount:</b> %s
            <b>Date:</b> %s
            <b>Category:</b> %s
            """;

    public static final String INCOME_MSG = EmojiParser.parseToUnicode(":heavy_plus_sign:") + """
            <b>Income</b>
            <b>Title:</b> %s
            <b>Description:</b> %s
            <b>Amount:</b> %s
            <b>Date:</b> %s
            <b>Category:</b> %s
            """;
    public static final String CATEGORY_ADD_TITLE_MSG = "Please type new category title:";

    public static final String CATEGORY_ADD_TYPE_MSG = "Please select new category type:";

    public static final String CATEGORY_CREATED_MSG = EmojiParser.parseToUnicode(":ok_hand:") + """
            <b>Success!</b>
            New %s Category created!
            <b>Title:</b> %s
            <b>Type:</b> %s
            """;

    public static final String EXPENSE_ADD_TITLE_DESCRIPTION_DATE_MSG = """
            Please type your expense <b>date</b>, <b>title</b> and <b>description</b> with '-'
            For date use pattern: dd/mm/yyyy
            Don't add date if it's today expense.
            For example: <i>02/12/2003 - Food - berries and fruits</i> or
            <i>Food - berries and fruits</i>
            Description is optional.
            """;

    public static final String INCOME_ADD_TITLE_DESCRIPTION_DATE_MSG = """
            Please type your income <b>date</b>, <b>title</b> and <b>description</b> with '-'
            For date use pattern: dd/mm/yyyy
            Don't add date if it's today income.
            For example: <i>02/12/2022 - Salary - December Salary</i> or
            <i>Salary - December Salary</i>
            Description is optional.
            """;

    public static final String EXPENSE_ADD_AMOUNT_MSG = "Please type your expense amount:";

    public static final String EXPENSE_SELECT_CURRENCY_MSG = "Please select your expense currency:";

    public static final String EXPENSE_SELECT_CATEGORY_MSG = "Please select your expense category:";

    public static final String INCOME_ADD_AMOUNT_MSG = "Please type your income amount:";

    public static final String INCOME_SELECT_CURRENCY_MSG = "Please select your income currency:";

    public static final String INCOME_SELECT_CATEGORY_MSG = "Please select your income category:";

    public static final String BALANCE_ENTITY_CREATED_MSG = EmojiParser.parseToUnicode(":ok_hand:") + """
            <b>Success!</b>
            New %s created!
            <b>Title:</b> %s
            <b>Description:</b> %s
            <b>Amount:</b> %s
            <b>Date:</b> %s
            <b>Category:</b> %s
            """;

    public static final String BALANCE_ENTITY_CREATED_NOTIFICATION_MSG = EmojiParser.parseToUnicode(":bell:") + """
            <b>Hi %s!</b>
            %s has just created a new %s:
            <b>Title:</b> %s
            <b>Description:</b> %s
            <b>Amount:</b> %s
            <b>Date:</b> %s
            <b>Category:</b> %s
            """;


    public static final String INFO_MSG = EmojiParser.parseToUnicode(":wave:") + """
            <b>Hi, %s! </b>
            <b>This is an Expense Tracker Bot!</b>
            <b>Only for authorized users!</b>
            You can manage your expenses and see your balance.
            Use these commands:
            /info - Get info
            /add_expense - Add new expense
            /add_income - Add new income
            /add_expense_category - Add new expense category
            /add_income_category - Add new income category
            /month_balance - See current month balance
            /total_balance - See total balance
            /latest_expenses - See the 20 latest expenses
            /latest_incomes - See the 20 latest incomes
            """;

    public static final String GENERIC_ERROR_MSG = EmojiParser.parseToUnicode(":confused:") + """ 
            Sorry, an unexpected error occurred. Our team has been notified. Please try again later.
            """;

    public static final String INVALID_INCOME_CATEGORY_NAME_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Category title is required!
            Please start again with command
            /add_income_category
            """;

    public static final String INVALID_EXPENSE_CATEGORY_NAME_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Category title is required!
            Please start again with command
            /add_expense_category
            """;

    public static final String INVALID_EXPENSE_DATE_TITLE_DESC_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Invalid input!
            Please start again with command
            /add_expense
            """;

    public static final String INVALID_EXPENSE_AMOUNT_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Invalid amount!
            You need to provide a number!
            Please start again with command
            /add_expense
            """;

    public static final String INVALID_INCOME_DATE_TITLE_DESC_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Invalid input!
            Please start again with command
            /add_income
            """;

    public static final String INVALID_INCOME_AMOUNT_ERROR_MSG = EmojiParser.parseToUnicode(":stop_sign:") + """ 
            Invalid amount!
            You need to provide a number!
            Please start again with command
            /add_income
            """;

    public static final String NOT_AUTHORIZED_ERROR_MSG = EmojiParser.parseToUnicode(":no_entry:") + """ 
            This bot is for authorized users only!
            """;

    public static final String EXPENSE = "Expense";
    public static final String INCOME = "Income";
}
