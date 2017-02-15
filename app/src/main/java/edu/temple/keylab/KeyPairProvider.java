package edu.temple.keylab;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairProvider extends ContentProvider {

    public KeyPairProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        String[] columnNames = {"privateKey", "publicKey"};
        MatrixCursor newKeyPair = new MatrixCursor(columnNames);

        newKeyPair.addRow(generateNewPair());

        return newKeyPair;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String[] generateNewPair() {

        /**Returns a 2 element string array where the first element is a new Private key, and
        the second element is a new Public key**/

        KeyPairGenerator keyPairGenerator = null;
        KeyPair keyPair;
        String[] newPair = {null, null};

        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyPair = keyPairGenerator.generateKeyPair();

        //Change Private Key to String
        byte[] privateKeyBytes = Base64.encode(keyPair.getPrivate().getEncoded(),Base64.DEFAULT);
        String privateKey = new String(privateKeyBytes);

        //Change Public Key to String
        byte[] publicKeyBytes = Base64.encode(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
        String publicKey = new String(publicKeyBytes);

        newPair[0] = privateKey;
        newPair[1] = publicKey;

        return newPair;

    }

}
