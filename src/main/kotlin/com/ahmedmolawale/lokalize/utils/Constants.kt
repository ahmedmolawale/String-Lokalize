package com.ahmedmolawale.lokalize.utils

const val NO_FILE_SELECTED = "Please select a file."
const val NO_VALID_DIRECTORY_SELECTED = "Please select a valid directory to save the output."
const val INVALID_SPREADSHEET_FILE_FORMAT = "Only .xls and .xlsx files are allowed."
const val INVALID_XML_FILE_FORMAT = "Only .xml file is allowed."
const val INVALID_SPREADSHEET = "Please select a valid spreadsheet file."
const val INVALID_XML = "Please select a valid xml file."
const val INVALID_KEY_ON_SPREADSHEET = "The first column in the first row must be named \"key\".\n" +
        "Please check the template file for reference."
const val NO_LANGUAGE_COLUMN_ON_SPREADSHEET = "The spreadsheet must have at least one language column." +
        "\nPlease check the template file for reference."
const val INVALID_LANGUAGE_COLUMN_ON_SPREADSHEET = "The language with code %s in column %d is " +
        "not supported to be used as an Android resource locale.\n" +
        "If you think this should be allowed. Please open an issue on the repository."