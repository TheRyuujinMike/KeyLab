package edu.temple.keylab;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class MainActivity extends AppCompatActivity {

    Button encryptButton, decryptButton, generateKey;
    EditText inputText;
    EditText resultText;
    PrivateKey privateKey;
    PublicKey publicKey;
    String inputTextHolder, resultTextHolder;

    KeyPairProvider keyPairProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encryptButton = (Button) findViewById(R.id.encryptButton);
        decryptButton = (Button) findViewById(R.id.decryptButton);
        generateKey = (Button) findViewById(R.id.generateKey);

        inputText = (EditText) findViewById(R.id.inputText);
        resultText = (EditText) findViewById(R.id.resultText);

        inputTextHolder = inputText.getText().toString();
        resultTextHolder = resultText.getText().toString();

        keyPairProvider = new KeyPairProvider();

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputTextHolder = inputText.getText().toString();

                try {
                    Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                    input.init(Cipher.ENCRYPT_MODE, publicKey);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
                    cipherOutputStream.write(inputTextHolder.getBytes("UTF-8"));
                    cipherOutputStream.close();

                    byte[] vals = outputStream.toByteArray();
                    resultTextHolder = new String(Base64.encodeToString(vals, Base64.DEFAULT));

                }
                catch (Exception e) {
                    Log.e("RSA", "Encryption Error");
                }

                resultText.setText(resultTextHolder);

            }

        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resultTextHolder = resultText.getText().toString();

                try {
                    Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                    output.init(Cipher.DECRYPT_MODE, privateKey);

                    CipherInputStream cipherInputStream = new CipherInputStream(
                            new ByteArrayInputStream(Base64.decode(resultTextHolder, Base64.DEFAULT)), output);
                    ArrayList<Byte> values = new ArrayList<>();
                    int nextByte;
                    while ((nextByte = cipherInputStream.read()) != -1) {
                        values.add((byte)nextByte);
                    }

                    byte[] bytes = new byte[values.size()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = values.get(i).byteValue();
                    }

                    inputTextHolder = new String(bytes, 0, bytes.length, "UTF-8");
                }
                catch (Exception e) {
                    Log.e("RSA", "Decryption Errpr");
                }

                inputText.setText(inputTextHolder);

            }
        });

        generateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor newKeyPair = keyPairProvider.query(null, null, null, null, null);

                newKeyPair.moveToFirst();

                String privateKeyString = newKeyPair.getString(newKeyPair.getColumnIndex("privateKey"));
                String publicKeyString = newKeyPair.getString(newKeyPair.getColumnIndex("publicKey"));

                Log.d("Key", privateKeyString + ", " + publicKeyString);


                try {
                    byte [] privateBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    privateKey = keyFactory.generatePrivate(keySpec);
                }
                catch(Exception e) {
                    Log.e("RSA", "RSA Private key error");
                }

                try {
                    byte[] publicBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    publicKey = keyFactory.generatePublic(spec);
                }
                catch(Exception e) {
                    Log.e("RSA", "RSA Public key error");
                }

            }
        });

    }
}
