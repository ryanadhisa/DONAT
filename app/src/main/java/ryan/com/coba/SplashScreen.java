
        package ryan.com.coba;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.content.Intent;
        import android.widget.Button;
        import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    //int z;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        //final int x = 3;
        //final int y = 3232; //deklarasi final = tidak dapat dirubah lagi

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button btn = (Button)findViewById(R.id.button1); //deklarasi view yang digunakan dgn nama variabel btn
        //final TextView txt = (TextView) findViewById(R.id.text1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                // hitung(x,y); //panggil fungsi liar

                //txt.setText(Integer.toString(z)); //set txt hello world untuk menampilkan hasil fungsi hitung (z)

                Intent donat = new Intent(SplashScreen.this, FungsiUtama.class); //intent kusus untuk pindah activity
                startActivity(donat);
            }


        });
    }

    // public void hitung(int x, int y){ //fungsi liar
    // z = x + y;
    //}
}

