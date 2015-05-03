package pl.openpkw.openpkwmobile.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.models.Commission;

/**
 * Created by kuczmysz on 02.05.2015.
 */
public class CommissionsArrayAdapter extends ArrayAdapter<Commission> {

    private Activity context;
    private List<Commission> items;
    private int selectedId;

    public CommissionsArrayAdapter(Context context, int resource, List<Commission> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.items = objects;
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
    }

    public int getSelectedId() {
        return selectedId;
    }

    public List<Commission> getItems() {
        return items;
    }

    public Commission getSelected() {
        for(Commission commision : items){
            if(commision.getId()==getSelectedId()){
                return commision;
            }
        }
        return null;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_commission, null);

            CommissionHolder viewHolder = new CommissionHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.commissions_row_name);
            viewHolder.address = (TextView) rowView.findViewById(R.id.commissions_row_address);
            viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.commissions_row_checkbox);
            rowView.setTag(viewHolder);
        }

        CommissionHolder holder = (CommissionHolder) rowView.getTag();
        final Commission commission = items.get(position);
        holder.name.setText(commission.getName());
        holder.address.setText(commission.getAddress());
        holder.checkBox.setChecked(commission.getId() == selectedId);

        return rowView;
    }

    static class CommissionHolder {
        CheckBox checkBox;
        TextView name;
        TextView address;
    }
}
