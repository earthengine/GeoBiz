package me.earthengine.geobiz.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.earthengine.geobiz.ApplicationContextProvider;
import me.earthengine.geobiz.business.Customer;
import me.earthengine.geobiz.business.CustomerContent;

/**
 * Created by earth_000 on 2015/6/11.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_PATH="/data/data/me.earthengine.geobiz/databases/";
    private static final String DB_NAME="crm_db";
    private static final int DB_VERSION=2;

    private static SQLiteDatabase database;
    private static DatabaseManager instance= null;

    public DatabaseManager() {
        super(ApplicationContextProvider.getContext(), DB_NAME, null, DB_VERSION);
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
        try {
            createDatabase();
            openDatabase();
        } catch (SQLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion==DB_VERSION){
            try {
                copyDatabase();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Select method
     *
     * @param query select query
     * @return - Cursor with the results
     * @throws android.database.SQLException sql exception
     */
    public Cursor select(String query) throws SQLException {
        return database.rawQuery(query, null);
    }

    public void AddCustomers() {
        Cursor c = database.rawQuery("select * \n" +
                "\t`id`,\n" +
                "\t`firstName`,\n" +
                "\t`lastName`,\n" +
                "\t`nameFormat`,\n" +
                "\t`suburb`,\n" +
                "\t`street`,\n" +
                "\t`streetType`,\n" +
                "\t`streetNo`,\n" +
                "\t`frequency`,\n" +
                "\t`dueDay`,\n" +
                "\t`amount`,\n" +
                "\t`contactNumber`,\n" +
                "\t`email`,\n" +
                "\t`facebook`\n" +
                "from `Customers`", null);
        if(!c.moveToFirst())
        do{
            Customer cu = new Customer(c.getInt(0),c.getString(1));
            cu.lastName = c.getString(2);
            cu.firstNameLast = c.getInt(3)==1;
            cu.suburb = c.getString(4);
            cu.street = c.getString(5);
            cu.streetType = c.getString(6);
            cu.streetNo = c.getString(7);
            cu.frequency = c.getString(8);
            cu.dueDay = c.getInt(9);
            cu.amount = new BigDecimal(c.getDouble(10));
            cu.contactNumber = c.getString(11);
            cu.email = c.getString(12);
            cu.facebook = c.getString(13);
            CustomerContent.addItem(cu);
        }while(c.moveToNext());
    }
}
