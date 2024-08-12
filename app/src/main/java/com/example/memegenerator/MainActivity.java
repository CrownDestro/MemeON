package com.example.memegenerator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private EditText topTextInput, bottomTextInput;
    private TextView textView1, textView2, uploadText;
    private ImageView imageView;
    private SeekBar sizeSeekBar;
    private LinearLayout colorButtons;
    private Bitmap selectedImage;
    private int textColor = 0xFFFFFFFF;
    private float textSize  = 20f;
    private Spinner fontSpinner;
    private String[] fontNames = {"Default", "Serif", "Monospace", "Cursive", "Monaco", "Verdana"};
    private Typeface[] typefaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topTextInput = findViewById(R.id.topTextInput);
        bottomTextInput = findViewById(R.id.bottomTextInput);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        uploadText = findViewById(R.id.uploadText);
        imageView = findViewById(R.id.imageView);
        sizeSeekBar = findViewById(R.id.sizeSeekBar);
        colorButtons = findViewById(R.id.colorButtons);
        fontSpinner = findViewById(R.id.fontSpinner);
        typefaces = new Typeface[]{
                Typeface.DEFAULT,
                Typeface.SERIF,
                Typeface.MONOSPACE,
                Typeface.create("cursive", Typeface.NORMAL),
                Typeface.create("monaco", Typeface.NORMAL),
                Typeface.create("verdana", Typeface.NORMAL),
        };

        Button tryButton = findViewById(R.id.tryButton);
        Button loadButton = findViewById(R.id.loadButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button sizeButton = findViewById(R.id.sizeButton);
        Button shareButton = findViewById(R.id.shareButton);

        Button colorButton = findViewById(R.id.colorButton);
        Button redButton = findViewById(R.id.redButton);
        Button blackButton = findViewById(R.id.blackButton);
        Button yellowButton = findViewById(R.id.yellowButton);
        Button greenButton = findViewById(R.id.greenButton);
        Button blueButton = findViewById(R.id.blueButton);
        Button cyanButton = findViewById(R.id.cyanButton);
        Button grayButton = findViewById(R.id.grayButton);

        tryButton.setOnClickListener(v -> updateImage());

        loadButton.setOnClickListener(v -> openImageChooser());

        saveButton.setOnClickListener(v -> saveImage());

        shareButton.setOnClickListener(v -> shareImage("Meme"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Typeface selectedFont = typefaces[position];
                updateImageWithFont(selectedFont);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        sizeButton.setOnClickListener(v -> {
            if (sizeSeekBar.getVisibility() == View.GONE) {
                sizeSeekBar.setVisibility(View.VISIBLE);
                colorButtons.setVisibility(View.GONE);
            } else {
                sizeSeekBar.setVisibility(View.GONE);
            }
        });

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSize = progress + 20;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        colorButton.setOnClickListener(v -> {
            if (colorButtons.getVisibility() == View.GONE) {
                colorButtons.setVisibility(View.VISIBLE);
                sizeSeekBar.setVisibility(View.GONE);
            } else {
                colorButtons.setVisibility(View.GONE);
            }
        });

        redButton.setOnClickListener(v -> {
            textColor = Color.RED;
            updateImage();
        });

        blackButton.setOnClickListener(v -> {
            textColor = Color.BLACK;
            updateImage();
        });

        yellowButton.setOnClickListener(v -> {
            textColor = Color.YELLOW;
            updateImage();
        });

        greenButton.setOnClickListener(v -> {
            textColor = Color.GREEN;
            updateImage();
        });

        blueButton.setOnClickListener(v -> {
            textColor = Color.BLUE;
            updateImage();
        });

        cyanButton.setOnClickListener(v -> {
            textColor = Color.CYAN;
            updateImage();
        });

        grayButton.setOnClickListener(v -> {
            textColor = Color.GRAY;
            updateImage();
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImage);
                uploadText.setVisibility(View.GONE);
                updateImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateImageWithFont(Typeface typeface) {
        if (selectedImage != null) {
            Bitmap updatedBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(updatedBitmap);

            String topText = topTextInput.getText().toString();
            String bottomText = bottomTextInput.getText().toString();

            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setTypeface(typeface);

            int width = updatedBitmap.getWidth();
            int height = updatedBitmap.getHeight();

            canvas.drawText(topText, (float) width / 2, textSize + 20, paint);
            canvas.drawText(bottomText, (float) width / 2, height - 20, paint);

            imageView.setImageBitmap(updatedBitmap);
        }
    }


    private void updateImage() {
        if (selectedImage != null) {
            Bitmap updatedBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(updatedBitmap);

            String topText = topTextInput.getText().toString();
            String bottomText = bottomTextInput.getText().toString();

            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);

            int width = updatedBitmap.getWidth();
            int height = updatedBitmap.getHeight();

            canvas.drawText(topText, (float) width / 2, textSize + 20, paint);
            canvas.drawText(bottomText, (float) width / 2, height - 20, paint);

            imageView.setImageBitmap(updatedBitmap);
            Typeface selectedFont = typefaces[fontSpinner.getSelectedItemPosition()];
            updateImageWithFont(selectedFont);
        }
    }

    private Bitmap createMemeBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the ImageView onto the canvas
        imageView.draw(canvas);

        // Draw text from textView1 onto the canvas
        Paint paint = new Paint();
        paint.setColor(textView1.getCurrentTextColor());
        paint.setTextSize(textView1.getTextSize());
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textView1.getText().toString(), (float) canvas.getWidth() / 2, textView1.getY() + (float) textView1.getHeight(), paint);

        // Draw text from textView2 onto the canvas
        paint.setColor(textView2.getCurrentTextColor());
        paint.setTextSize(textView2.getTextSize());
        canvas.drawText(textView2.getText().toString(), (float) canvas.getWidth() / 2, canvas.getHeight() - (float) textView2.getHeight() / 2, paint);

        return bitmap;
    }

    private void saveImage() {
        Bitmap bitmap = createMemeBitmap();
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "Meme","Image by MemeON");

        if (savedImageURL != null) {
            Uri savedImageURI = Uri.parse(savedImageURL);
            new AlertDialog.Builder(this)
                    .setTitle("Image Saved")
                    .setMessage("Image saved successfully: " + savedImageURI.toString())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Error saving image")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void shareImage(String title) {
        Bitmap bitmap = createMemeBitmap();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, null);
        Uri uri = Uri.parse(path);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}
