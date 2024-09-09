package com.example.ecommerce.ui.customers.create_customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentChoosePhotoBottomSheetBinding;
import com.example.ecommerce.utils.FileHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChoosePhotoBottomSheetFragment extends BottomSheetDialogFragment {

    private OnPhotoChoosedCallback callback;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private Uri imageUri;

    public static ChoosePhotoBottomSheetFragment newInstance(OnPhotoChoosedCallback callback) {
        ChoosePhotoBottomSheetFragment fragment = new ChoosePhotoBottomSheetFragment();
        fragment.callback = callback;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_photo_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChoosePhotoBottomSheetBinding binding = FragmentChoosePhotoBottomSheetBinding.bind(view);

        // Camera intent launcher for taking a picture
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                Log.d("ChoosePhotoBottomSheetFragment", "Image saved to: " + imageUri);
                callback.onSuccessfulPhotoChoose(imageUri);
            } else {
                Log.d("ChoosePhotoBottomSheetFragment", "Image capture failed");
                callback.onFailedPhotoChoose("Image capture failed");
            }
        });

        // Trigger camera capture when the user clicks the button
        binding.lyTakePhoto.setOnClickListener(v -> {
            imageUri = createImageUri(); // Create the URI for the image storage
            if (imageUri != null) {
                takePictureLauncher.launch(imageUri);
            } else {
                Log.e("ChoosePhotoBottomSheetFragment", "Failed to create image URI");
                callback.onFailedPhotoChoose("Failed to create image URI");
            }
        });

        // Pick image intent launcher for selecting an image from the gallery
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
            if (result != null) {
                Log.d("ChoosePhotoBottomSheetFragment", "Image selected from gallery: " + result);
                String filePath = FileHelper.saveImage(result, "customer_photo", App.appModule.provideAppContext());
                Uri uri = Uri.parse(filePath);
                callback.onSuccessfulPhotoChoose(uri);
            } else {
                Log.d("ChoosePhotoBottomSheetFragment", "Image selection failed");
                callback.onFailedPhotoChoose("Image selection failed");
            }
        });

        // Trigger image selection when the user clicks the button
        binding.lyChooseGallery.setOnClickListener(v -> {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Remove the image when the user clicks the button
        binding.lyRemovePhoto.setOnClickListener(v -> {
            try{
                FileHelper.deleteImage(imageUri.toString(), "customer_photo");
                callback.onPhotoRemoved();
            } catch (Exception e){
                Log.e("ChoosePhotoBottomSheetFragment", "Failed to delete image: " + e.getMessage());
                callback.onFailedPhotoChoose("Failed to delete image");
            }
        });
    }

    // Create the URI for the image storage
    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Customer photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Customer photo taken from camera");
        return requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}