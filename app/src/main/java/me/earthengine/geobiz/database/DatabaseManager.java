package me.earthengine.geobiz.database;

import android.content.Context;
import android.content.res.AssetManager;
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

    private static final String DB_NAME="crm_db";
    private static final int DB_VERSION=2;

    private static SQLiteDatabase database;
    private static DatabaseManager instance= null;

    public DatabaseManager() {
        super(ApplicationContextProvider.getContext(), DB_NAME, null, DB_VERSION);
        try {
            if(!checkDatabase())
                copyDatabase();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        database = getWritableDatabase();
    }

    private boolean checkDatabase(){
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            Cursor c = db.rawQuery("select count(*) from Customers",null);
            c.moveToFirst();
            return c.getInt(0)>0;
        } finally {
            db.close();
        }

    }

    public static DatabaseManager instance(){
        if(instance==null)
            instance = new DatabaseManager();
        return instance;
    }

    public void copyDatabase() throws IOException{
        InputStream myInput = null;
        try{
            Context ctx = ApplicationContextProvider.getContext();
            AssetManager am = ctx.getAssets();
            myInput = am.open(DB_NAME);

            // Path to the just created empty db
            String outFileName = ctx.getDatabasePath(DB_NAME).getAbsolutePath();

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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.rawQuery("CREATE TABLE Customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "nameFormat INTEGER DEFAULT 1," +
                "suburb TEXT, " +
                "street TEXT, " +
                "streetType TEXT, " +
                "streetNo TEXT, " +
                "frequency TEXT, " +
                "dueDay INTEGER, " +
                "amount NUMERIC, " +
                "contactNumber TEXT, " +
                "email TEXT, " +
                "facebook TEXT" +
                ");",null);

        db.setVersion(DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion==DB_VERSION){
            try {
                copyDatabase();
            } catch (IOException ex){
                ex.printStackTrace();
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
        String query = "select " +
                "id," +
                "firstName," +
                "lastName," +
                "nameFormat," +
                "suburb," +
                "street," +
                "streetType," +
                "streetNo," +
                "frequency," +
                "dueDay," +
                "amount," +
                "contactNumber," +
                "email," +
                "facebook " +
                "from Customers";
        Cursor c = database.rawQuery(query, null);
        if(!c.moveToFirst())
            return;
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
