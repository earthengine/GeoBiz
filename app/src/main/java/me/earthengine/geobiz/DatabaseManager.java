package me.earthengine.geobiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by earth_000 on 2015/6/11.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_PATH="/data/data/me.earthengine.geobiz/databases/";
    private static final String DB_NAME="crm_db";
    private static final int DB_VERSION=1;

    private static SQLiteDatabase database;
    private static DatabaseManager instance= null;

    public DatabaseManager() {
        super(ApplicationContextProvider.getContext(), DB_NAME, null, DB_VERSION);

        try {
            createDatabase();
            openDatabase();
        } catch (SQLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static DatabaseManager instance(){
        if(instance==null)
            instance = new DatabaseManager();
        return instance;
    }

    private void createDatabase() throws IOException{
        boolean haveDb = checkDatabase();
        if(haveDb)
            return;

        this.getReadableDatabase();
        try{
            copyDatabase();
        } catch(IOException e){
            throw new Error("Error copying database");
        }
    }

    public void copyDatabase() throws IOException{
        InputStream myInput = null;
        try{
            myInput = ApplicationContextProvider.getContext().getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            OutputStream myOutput = null;

            // Open the empty db as the output stream
            try {
                myOutput = new FileOutputStream(outFileName);
                // transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                // Close the streams
                myOutput.flush();
            } finally {
                if(myOutput!=null)
                    myOutput.close();
            }

        } finally {
            if(myInput!=null)
                myInput.close();
        }

    }

    private boolean checkDatabase(){
        SQLiteDatabase db = null;
        try {
            String path = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
            return true;
        } catch (SQLiteException e){
            return false;
        } finally {
            if(db!=null)
                db.close();
        }
    }

    private void openDatabase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
