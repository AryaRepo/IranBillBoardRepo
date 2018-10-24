package city.bilboard.aryasoft.com.bilboardcity.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
/**
 * Created by Mac_Dev on 11/11/2017.
 */

public class MenuDialog extends android.support.v4.app.DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();

        View mView=null;//inflater.inflate(R.layout.dialog_menu_layout,null);
        builder.setView(mView);

        return  builder.create();
    }
}
