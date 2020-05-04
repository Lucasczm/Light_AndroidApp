package br.com.maplus.light;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;

import java.net.InetAddress;

import br.com.maplus.light.Utils.WifiModel;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    WifiModel wifiModel;
    SliderAdapter(Context context, WifiModel wifiModel) {
        this.context = context;
        this.wifiModel = wifiModel;
    }

    int pages = 3;
    int currentStep = 0;
    InetAddress IP;
    public boolean DHCP;
    boolean fail;

    String[][] texts = {
            {"Para continuar vamos conectar na sua rede WiFi!", "O Dispositivo só funciona com redes WiFi 2.4ghz"},
            {"Pressione o botão de pareamento no dispositivo e aguarde", "Procurando dispositivo..."},
            {"Você quer utilizar IP automático ou fixo?", "Para o funcionamento offilne você deve definir um IP fixo."},
            {"Terminando a configuração", "Configurando o IP..."}
    };

    @Override
    public int getCount() {
        return pages;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view;
        Log.d("LIGHTAPP", "PAGE INSTANCE + "+ position);
        switch (position) {
            case 1:
                view = ConfigureWizardSetup(container);
                break;
            case 2:
                view = ConfigureWizardFinish(container);
                break;
            default:
                view = ConfigureWizardWelcome(container);
                break;
        }
        container.addView(view);

        return view;
    }

    private View ConfigureWizardWelcome(ViewGroup container) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.wizard_welcome_layout, container, false);
        Button continuebtn = view.findViewById(R.id.nextButton);
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WizardActivity ma = (WizardActivity) context;
                ma.nextPage();
            }
        });
        return view;
    }

    private View ConfigureWizardSetup(final ViewGroup container) {
        final WizardActivity parent = (WizardActivity) context;

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.wizard_slide_layout, container, false);
        view.setTag("wizardView");
        final Button continuebtn = view.findViewById(R.id.continueButton);
        final Button ipConfirmBtn = view.findViewById(R.id.ipConfirmButton);
        final EditText wifiPassword = view.findViewById(R.id.wifiPassword_input);
        final EditText tokenEditText = view.findViewById(R.id.deviceCodeEditText_wizard);
        final EditText ipEditText = view.findViewById(R.id.ip_wizardEditText);

        TextView wifiNameText = view.findViewById(R.id.wifiName);
        wifiNameText.setText(wifiModel.getSsid());

        Spinner ipSelect = view.findViewById(R.id.ip_select);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.ip_select, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ipSelect.setAdapter(adapter);

        ipSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DHCP = i == 0;
                ipEditText.setEnabled(!DHCP);
                Drawable buttonDrawable = ipEditText.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                int color = (!DHCP) ? Color.WHITE : Color.parseColor("#C6C6C6");
                DrawableCompat.setTint(buttonDrawable, color);
                ipEditText.setBackground(buttonDrawable);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewButton) {
                if (currentStep == 0) {
                    parent.configureEspTouch(wifiPassword.getText().toString(), tokenEditText.getText().toString(), new IEsptouchListener() {
                        @Override
                        public void onEsptouchResultAdded(IEsptouchResult result) {
                            fail = !result.isSuc();
                            currentStep = result.isSuc() ? currentStep + 1 : currentStep - 1;
                            IP = result.getInetAddress();
                            UpdatePage(view, currentStep);
                        }
                    });
                }
                currentStep++;
                UpdatePage(view, currentStep);
            }
        });

        ipConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewButton) {
                currentStep++;
                UpdatePage(view, currentStep);
                parent.ConfigureIP(DHCP, ipEditText.getText().toString());
            }
        });

        return view;
    }

    private void UpdatePage(View view, int postion) {
        if (postion > 3) return;
        final RelativeLayout headerLayout = view.findViewById(R.id.header_wizard_layout);
        final RelativeLayout wifiLayout = view.findViewById(R.id.wifi_wizard_layout);
        final RelativeLayout ipLayout = view.findViewById(R.id.ip_wizard_layout);
        final TextView titleText = view.findViewById(R.id.wizard_slide_title);
        final TextView subtitle = view.findViewById(R.id.wizard_slide_subtitle);
        EditText ipEditText = view.findViewById(R.id.ip_wizardEditText);

        LayoutParams layoutParams = (LayoutParams) headerLayout.getLayoutParams();
        layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
        switch (postion) {
            case 0:
                wifiLayout.setVisibility(View.VISIBLE);
                ipLayout.setVisibility(View.INVISIBLE);
                titleText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 1:
                ipLayout.setVisibility(View.INVISIBLE);
                wifiLayout.setVisibility(View.INVISIBLE);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            case 2:
                ipLayout.setVisibility(View.VISIBLE);
                wifiLayout.setVisibility(View.INVISIBLE);
                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ipEditText.setText(IP != null ? IP.getHostAddress() : "");
                break;
            case 3:
                ipLayout.setVisibility(View.INVISIBLE);
                wifiLayout.setVisibility(View.INVISIBLE);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
        }
        titleText.setText(texts[postion][0]);
        subtitle.setText(texts[postion][1]);
        headerLayout.setLayoutParams(layoutParams);

        if (postion == 0 && fail) {
            titleText.setText("Ocorreu um erro ao configurar. Tente novamente!");
        }
    }

    private View ConfigureWizardFinish(ViewGroup container) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.wizard_finish_layout, container, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((RelativeLayout) view);
    }

}
