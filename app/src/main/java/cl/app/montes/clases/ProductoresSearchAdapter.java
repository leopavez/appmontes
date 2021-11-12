package cl.app.montes.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;

public class ProductoresSearchAdapter extends ArrayAdapter<Productores> {

    private Context context;
    private int LIMIT = 5;
    private List<Productores> productores;

    public ProductoresSearchAdapter(Context context, List<Productores> productores){
        super(context, R.layout.productores_search, productores);
        this.context = context;
        this.productores = productores;
    }


    @Override
    public int getCount(){
        return Math.min(LIMIT, productores.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.productores_search,null);
        Productores vh = productores.get(position);
        TextView textViewProductores = view.findViewById(R.id.textviewProductorSearch);
        textViewProductores.setText(vh.getRazon_social());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new ProductorFilter(this,context);
    }

    private class ProductorFilter extends Filter {
        private ProductoresSearchAdapter productoresSearchAdapter;
        private Context context;

        public ProductorFilter(ProductoresSearchAdapter productoresSearchAdapter, Context context){
            super();
            this.productoresSearchAdapter = productoresSearchAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            productoresSearchAdapter.productores.clear();
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0){
                filterResults.values = new ArrayList<>();
                filterResults.count = 0;

            }else{
                DatabaseHelper db = new DatabaseHelper(context);
                List<Productores> patents = db.search(charSequence.toString());
                filterResults.values = patents;
                filterResults.count = patents.size();

            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            productoresSearchAdapter.productores.clear();
            if ((List<Productores>)filterResults.values != null){
                productoresSearchAdapter.productores.addAll((List<Productores>)filterResults.values);
                productoresSearchAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Productores vehiculos = (Productores) resultValue;
            return vehiculos.getRazon_social();
        }
    }
}
