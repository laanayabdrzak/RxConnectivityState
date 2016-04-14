# RxConnectivityState
Useful Android library to check connectivity state within your app 

Include the following dependency in your build.gradle file.

**Gradle:**
```Gradle
    dependencies {
        compile 'com.???'
    }
```

## V1.0.0

stable version.

## Why use it 

It is better to check internet connectivity status before making any HTTP Requests to avoid http exceptions. this libray is created with the powerful of Reactive extension.

## Usage

*For a working implementation of this library, just subscribe to observable* Like this:

MainActivity:

```java
public class MainActivity extends AppCompatActivity {

    private TextView status;
    private Subscription mSubscription;
    private RxConnectivityState mRxConnectivityState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.state);
        mRxConnectivityState = new RxConnectivityState();
        mSubscription = mRxConnectivityState.observeConnectivity(getApplicationContext())
                                 .subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(new Action1<UtilityNetworkState>() {
                                     @Override
                                     public void call(UtilityNetworkState utilityNetworkState) {

                                            if (utilityNetworkState == UtilityNetworkState.WIFI_CONNECTED){
                                                status.setText("Connected");
                                            }
                                     }
                                 });
    }

    @Override
    protected void onPause() {
        mRxConnectivityState.safelyUnsubscribeToRxConnectivityState(mSubscription);
        super.onPause();
    }
}
```
## TODO

* to be pushed soon
* simplify the subscription 
* change code to annotation

## Developed By
 Author: **Abderrazak LAANAYA**

<a href="https://www.linkedin.com/in/laanayabdrzak">
  <img alt="Follow me on LinkedIn"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/linkedin.png" />
</a>
License
=======

    Copyright 2016 Abderrazak Laanaya

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

