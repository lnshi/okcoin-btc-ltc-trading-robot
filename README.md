## Description
  - This is a trading robot for the BTC/LTC trading platform 'OKCoin(www.okcoin.cn)'.

## How to use it directly
  - This is an AndroidStudio porject, so just import it into your AndroidStudio.
  - Set your OKCoin 'CN_API_KEY' and 'CN_API_SECRET' at: '/app/src/main/java/com/leonard/sg/okcoin/service/robot/constant/SyncConstants.java'.
  - Build it as an apk then install on your Android phone, then you can use it.
  
## About the default trading algorithm
  - You can just read the source code in 'app/src/main/java/com/leonard/sg/okcoin/strategy/coverage', it is a very simple strategy.

## About how to integrate your own trading algorithm
  - You can refer to the one in 'app/src/main/java/com/leonard/sg/okcoin/strategy/coverage'.
  
## NOTICE
  - **This project got halted due to the unbearable super long transaction API request delay when tried to access OKCoin(www.okcoin.cn) from outside of China Mainland.**


