package com.example.oteloxtfgdam.activity.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

    binding = FragmentGalleryBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("Artículo 1", "01/01/2022", "$10.00"));
        itemList.add(new Item("Artículo 2", "02/02/2022", "$20.00"));
        itemList.add(new Item("Artículo 3", "03/03/2022", "$30.00"));
        itemList.add(new Item("Artículo 4", "04/04/2022", "$40.00"));
        itemList.add(new Item("Artículo 5", "05/05/2022", "$50.00"));
        itemList.add(new Item("Artículo 6", "06/06/2022", "$60.00"));
        itemList.add(new Item("Artículo 7", "07/07/2022", "$70.00"));
        itemList.add(new Item("Artículo 8", "08/08/2022", "$80.00"));
        itemList.add(new Item("Artículo 9", "09/09/2022", "$90.00"));
        itemList.add(new Item("Artículo 10", "10/10/2022", "$100.00"));
        LinearLayout linearLayout = root.findViewById(R.id.linear_layout);

        for (Item item : itemList) {
            View itemView = inflater.inflate(R.layout.item_view, linearLayout, false);
            ImageView itemIcon = itemView.findViewById(R.id.item_icon);
            TextView itemName = itemView.findViewById(R.id.item_name);
            TextView itemDate = itemView.findViewById(R.id.item_date);
            TextView itemAmount = itemView.findViewById(R.id.item_amount);

            itemIcon.setImageResource(R.drawable.outline_home_24);
            itemName.setText(item.getName());
            itemDate.setText(item.getDate());
            itemAmount.setText(item.getAmount());

            linearLayout.addView(itemView);
        }
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}