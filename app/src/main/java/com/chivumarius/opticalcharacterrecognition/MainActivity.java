package com.chivumarius.opticalcharacterrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    // ▼ "CONSTANT" → FOR "PICKING" IMAGE FROM "GALLERY" ▼
    public static final int PICK_IMAGE = 123;


    // ▼ "DECLARATION" OF "WIDGETS IDS" ▼
    ImageView imageView;
    TextView textView;
    Button imageBTN, speachBTN;


    // ▼ "VARIABLES DECLARATION" → FROM "ML-KIT" ▼
    InputImage inputImage;
    TextRecognizer recognizer;
    TextToSpeech textToSpeech;
    public Bitmap textImage;







    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "ON CREATE()" METHOD ▼
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //// ▼ "INITIALIZE" OF "ML-KIT VARIABLES"  ▼
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        // ▼ "INITIALIZE" OF "WIDGETS IDS" ▼
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text);
        imageBTN = findViewById(R.id.image);
        speachBTN = findViewById(R.id.speech);



        // ▼ "FUNCTIONALITY" ▼
        // ▼ "ON CLICK LISTENER" FOR "CHOOSE IMAGE" BUTTON ▼
        imageBTN.setOnClickListener(new View.OnClickListener() {

            // ▼ "ON CLICK)_" METHOD ▼
            @Override
            public void onClick(View view) {
                // ▼ CALLING "METHOD" ▼
                openGallery();
            }
        });



        // ▼ "ON CLICK LISTENER" FOR "TEXT TO SPEECH" ▼
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            // ▼ "ON INIT()" METHOD ▼
            @Override
            public void onInit(int status) {

                // ▼ "CHECKING": IF "THERE IS NO ERROR" ▼
                if(status != TextToSpeech.ERROR) {

                    // ▼ "SETTING" THE "LANGUAGE" ▼
//                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setLanguage(new Locale("ro_RO"));
                    //textToSpeech.setLanguage(new Locale("fr_FR"));
                    //textToSpeech.setLanguage(new Locale("de_DE"));
                    // textToSpeech.setLanguage(new Locale("it_IT"));
                    //textToSpeech.setLanguage(new Locale("es_ES"));
                    // textToSpeech.setLanguage(new Locale("ja_JP"));
                    // textToSpeech.setLanguage(new Locale("ch_CH"));


                    // ▼ "SETTING" THE "TEXT READING SPEED" ▼
                    textToSpeech.setSpeechRate(1.00f);

                    // ▼ "SETTING" THE "VOICE THICKENING" / "THINNING" ▼
                    textToSpeech.setPitch(1.00f);

                }
            }
        });



        // ▼ "ON CLICK LISTENER" FOR "SPEECH" BUTTON ▼
        speachBTN.setOnClickListener(new View.OnClickListener() {

            // ▼ "ON CLICK)" METHOD ▼
            @Override
            public void onClick(View view) {

                // ▼ "LISTING" THE "SCANNED TEXT" ▼
                textToSpeech.speak(
                        textView.getText().toString(),
                        TextToSpeech.QUEUE_FLUSH,
                        null
                );
            }
        });
    }






    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "ON ACTIVITY RESULT()" METHOD
    //      → "REQUESTED" BY "START ACTIVITY FOR RESULT()" FUNCTION ▼
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ▼ "CHECKING": IF "RESULT IS OK" ▼
        if (requestCode == PICK_IMAGE) {

            // ▼ "CHECKING": IF "THERE IS" A "DATA" ▼
            if (data != null) {

                // ▼ "CREATING" A "BYTE ARRAY" ▼
                byte[] byteArray = new byte[0];

                // ▼ SETTING" THE "FILE PATH" ▼
                String filePath = null;


                // ▼ "CHECKING" THE "REQUEST CODE" ▼
                try {
                    // ▼ GETTING" THE "FILE PATH" ▼
                    inputImage = InputImage.fromFilePath(this, data.getData());

                    // ▼ GETTING" THE "BITMAP INTERNAL" FOR "INPUT IMAGE"▼
                    Bitmap resultUri = inputImage.getBitmapInternal();


                    // ▼ USING" THE "GLIDE" LIBRARY → TO "LOAD" THE "SELECTED IMAGE" ▼
                    Glide.with(MainActivity.this)
                            .load(resultUri)
                            .into(imageView);



                    // ▼ "READING TEXT" AND "PROCESSING" THE "INPUT IMAGE" ▼
                    Task<Text> result =
                            recognizer.process(inputImage)

                                    // ▼ "ON SUCCESS LISTENER()" METHOD ▼
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {

                                        // ▼ "ON SUCCESS()" METHOD ▼
                                        @Override
                                        public void onSuccess(Text text) {
                                            // ▼ CALLING" THE "METHOD" ▼
                                            processTextBlock(text);
                                        }
                                    })


                                    // ▼ "ON FAILURE LISTENER()" METHOD ▼
                                    .addOnFailureListener(
                                            new OnFailureListener() {

                                                // ▼ "ON FAILURE()" METHOD ▼
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // ▼ DISPLAYING" AN "`TOAST MANAGER" ▼
                                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }





    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "OPEN GALLERY()" METHOD
    //      → ALLOWS TO "CHOOSE IMAGE" → FROM "GALLERY" ▼
    private void openGallery() {

        // ▼ "REDIRECTING" THE "USER"
        //      → TO "CHOOSE IMAGE"
        //      → FROM "GALLERY"
        //      → TO "EXTRACT" THE "TEXT" FROM "IT" ▼
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/");

        // ▼ "ACCESSING" THE "EXTERNAL STORAGE" ▼
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");

        // ▼ "SELECTING" THE "IMAGE" ▼
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }







    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "PROCESS TEXT BLOCK()" METHOD ▼
    private void processTextBlock(Text result) {
        // ▼ STARTING "ML-KIT" → PROCESSING "TEXT BLOCK" ▼

        // ▼ GETTING" THE "TEXT" FROM "RESULT" ▼
        String resultText = result.getText();


        // ▼ "LOOPING" THROUGH "ALL" THE "BLOCKS"
        //      → INSIDE THE "TEXT"
        //      → AND "START EXTRACTING THEM"
        //      → INTO "INDIVIDUAL TEXT BLOCKS" ▼
        for (Text.TextBlock block: result.getTextBlocks()) {

            // ▼ EXTRACTING" EVERY "LINE"
            //      → FROM THE "IMAGE"
            //      → INSIDE THE "BLOCK TEXT"
            String blockText = block.getText();

            // ▼ ADDING" THE "NEW DATA" FOR THE "NEXT LINE" ▼
            textView.append("\n");


            // ▼ "GETTING" THE "CORNER POINTS" FROM" THE "BLOCK" ▼
            Point[] blockCornerPoints = block.getCornerPoints();  // ► "POINT" → FROM "ANDROID GRAPHICS" ◄

            // ▼ "GETTING" THE "BOUNDING BOX" FROM" THE "BLOCK"
            Rect blockFrame = block.getBoundingBox();  // ► "RECTANGLE" → FROM "ANDROID GRAPHICS" ◄



            // ▼ "LOOPING" THROUGH" ALL THE "LINES" INSIDE THE "BLOCK" ▼
            for (Text.Line line: block.getLines()) {

                // ▼ "EXTRACTING" EVERY "LINE TEXT" FROM THE "IMAGE" ▼
                String lineText = line.getText();

                // ▼ USING "ANDROID GRAPHICS"
                //      → TO ADD THE "LINE TEXT" → TO THE "TEXT VIEW" ▼
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();



                // ▼ "LOOPING" THROUGH "ALL" THE "ELEMENTS" INSIDE THE "LINE" ▼
                for (Text.Element element : line.getElements()) {
                    // ▼ ADDING" SPACES" TO THE "TEXT"
                    textView.append(" ");

                    // ▼ "EXTRACTING" THE "ELEMENT TEXT" FROM THE "IMAGE"
                    String elementText = element.getText();
                    textView.append(elementText);

                    // ▼ USING "ANDROID GRAPHICS"
                    //      → TO ADD THE "ELEMENT TEXT" → TO THE "TEXT VIEW"
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
    }




    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "ON PAUSE()" METHOD ▼
    @Override
    protected void onPause() {
        // ▼ CHECKING: IF "THERE IS NOT" ANY "TEXT TO SPEECH" ▼
        if(!textToSpeech.isSpeaking()){
            super.onPause();
        }
    }





    // ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    // ▼ "ON STOP()" METHOD ▼
    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}