package ua.pt.eventua.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.VCard;

import ua.pt.eventua.Constants;
import ua.pt.eventua.R;

public class BusinessCard extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.activity_business_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String current_username = this.getArguments().getString("current_username");
        String current_email = this.getArguments().getString("current_email");

        ImageView qrcode = (ImageView) getActivity().findViewById(R.id.qrcode);

        VCard lVCard=new VCard(current_email)
                .setEmail(current_email)
                .setName(current_username)
                .setWebsite("https://pt.linkedin.com/");
        Bitmap lBitmap1=QRCode.from(lVCard).bitmap();
        qrcode.setImageBitmap(lBitmap1);
        getActivity().setTitle(getString(R.string.title_business_card));

        // atualizar outros campos
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView mail = (TextView) view.findViewById(R.id.mail);
        ImageView image = (ImageView) view.findViewById(R.id.personImage);

        name.setText(Constants.CURRENT_PERSON.getName());
        mail.setText(Constants.CURRENT_PERSON.getMail());

        Glide.with(getActivity()).load(Constants.CURRENT_PERSON.getImageResource()).into(image);

    }

}
