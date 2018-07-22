package com.ahiru.rubicon;

import android.provider.BaseColumns;

public class TestContract {
    //空のコンストラクタ
    public TestContract(){}

    public static abstract class Choices implements BaseColumns {
        public static final String TABLE_NAME = "choices";
        public static final String COL_NAME = "choices";
        public static final String COL_TITLEID = "titleid";
    }

    public static abstract class Titles implements BaseColumns{
        public static final String TABLE_NAME = "titles";
        public static final String COL_NAME = "titles";
    }
}
