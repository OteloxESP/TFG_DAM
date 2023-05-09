package com.example.oteloxtfgdam.activity.ui.mercado;

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
import com.example.oteloxtfgdam.databinding.FragmentMercadoBinding;
import com.example.oteloxtfgdam.db.ItemDB;

import java.util.ArrayList;
import java.util.List;

public class MercadoFragment extends Fragment {

private FragmentMercadoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        MercadoViewModel mercadoViewModel =
                new ViewModelProvider(this).get(MercadoViewModel.class);

    binding = FragmentMercadoBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
        List<ItemDB> itemDBList = new ArrayList<>();
        itemDBList.add(new ItemDB("Artículo 1", "01/01/2022", "$10.00"));
        itemDBList.add(new ItemDB("Artículo 2", "02/02/2022", "$20.00"));
        itemDBList.add(new ItemDB("Artículo 3", "03/03/2022", "$30.00"));
        itemDBList.add(new ItemDB("Artículo 4", "04/04/2022", "$40.00"));
        itemDBList.add(new ItemDB("Artículo 5", "05/05/2022", "$50.00"));
        itemDBList.add(new ItemDB("Artículo 6", "06/06/2022", "$60.00"));
        itemDBList.add(new ItemDB("Artículo 7", "07/07/2022", "$70.00"));
        itemDBList.add(new ItemDB("Artículo 8", "08/08/2022", "$80.00"));
        itemDBList.add(new ItemDB("Artículo 9", "09/09/2022", "$90.00"));
        itemDBList.add(new ItemDB("Artículo 10", "10/10/2022", "$100.00"));
        LinearLayout linearLayout = root.findViewById(R.id.linear_layout);

        for (ItemDB itemDB : itemDBList) {
            View itemView = inflater.inflate(R.layout.item_view, linearLayout, false);
            ImageView itemIcon = itemView.findViewById(R.id.item_icon);
            TextView itemName = itemView.findViewById(R.id.item_name);
            TextView itemDate = itemView.findViewById(R.id.item_date);
            TextView itemAmount = itemView.findViewById(R.id.item_amount);

            itemIcon.setImageResource(R.drawable.outline_home_24);
            itemName.setText(itemDB.getName());
            itemDate.setText(itemDB.getDate());
            itemAmount.setText(itemDB.getAmount());

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