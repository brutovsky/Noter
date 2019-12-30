package com.brtvsk.noter.database;

public class NoteDBSchema {
    public static final class NoteTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String DESCRIPTION = "description";
            public static final String DATE = "date";
            public static final String MARKER = "marker";
            public static final String TEXT = "text";
            public static final String POSITION = "position";
        }
    }
}