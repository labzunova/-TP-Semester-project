package com.example.first.AccountEdit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public ImagesPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}


/*private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imagesRefs.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            LayoutInflater layoutinflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutinflater.inflate(R.layout.item_pager_gallery, null);
            image = view.findViewById(R.id.imageItemPager);

            // загрузка фото
            final long ONE_MEGABYTE = 1024 * 1024;
            StorageReference imageRef = imagesRefs.get(position);
            if (imageRef != null) {
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        // image.setImageBitmap(AccountEditActivity.resizeBitmap(bitmap, 500.0f));

                        Glide
                                .with(activity.getApplicationContext())
                                .load(AccountEditActivity.resizeBitmap(bitmap, 600.0f))
                                .into(image);

                        Log.d(FRAGMENT_TAG, "getButes onSuccess() listener on element " + position);
                        ((ViewPager) container).addView(view);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                    }
                });
            }


            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }
    }*/
