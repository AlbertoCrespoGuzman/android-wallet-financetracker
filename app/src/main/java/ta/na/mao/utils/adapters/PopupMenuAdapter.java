package ta.na.mao.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ta.na.mao.R;

public class PopupMenuAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web = {"OPÇÕES", "Editar meta", "Deletar meta", "Fechar"};
    private LayoutInflater mInflater;

    public PopupMenuAdapter(Activity context, int layout, String[] web) {
        super(context, R.layout.menu_popup_element, web);

        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Log.e("getView","posoitioin " + position);
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView= this.mInflater.inflate(R.layout.menu_popup_element, parent, false);
        LinearLayout bar = rowView.findViewById(R.id.adapter_menu_popup_element_layout);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.adapter_menu_popup_element_text);
        ImageView leftImageView = (ImageView) rowView.findViewById(R.id.adapter_menu_popup_element_image);
        ImageView rightImageView = rowView.findViewById(R.id.adapter_menu_popup_element_close);

        txtTitle.setText(web[position]);

        if(position == 0){
            bar.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            leftImageView.setVisibility(View.GONE);
            txtTitle.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            bar.setBackgroundColor(context.getResources().getColor(R.color.white));
            leftImageView.setVisibility(View.VISIBLE);
            if(position == 1 ) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pencil_primary_color));
                }else{
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pencil_primary_color_png));
                }

            }
            if(position == 2 ) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_trash_primary_color));
                }else{
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_trash_primary_color_png));
                }
            }
            if(position == 3 ) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_multiply));
                }else{
                    leftImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_multiply_png));
                }

            }

            rightImageView.setVisibility(View.GONE);
        }

        return rowView;
    }
}