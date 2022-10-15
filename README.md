# BloodTemp

https://github.com/vascarpenter/BloodTemp

- Androidでサーバに立てたapiへaccessして体温を記録しよう
  - サーバは自分で作ってね
- Jetpack Compose / retrofit2 / Hilt / Kotlin flowで書き直した
- かなり冗長だがなんとかならんもんか

### 問題点

- 日本語入力にATOKを入れている場合、Composeにおける 数字入力専用欄が ATOKのバグのために "." がIMEキーボード内に表示されない
  - EditTextの時は問題なかったのに。
  - Gboard なら問題なしなので ATOK をやめればよろしい
    - ATOKを入れてませんか?
  - キーボードを切り替えれば当然入力可能。

### このandroidアプリをコンパイルする前に

- build.gradle :app から ３つの文字列を参照しているので
- `~/.gradle/gradle.properties` に追加しておく

```
# 自分のサイトにあった設定に差し替えてね
bloodserverurl=https://ogehage.tk
bloodgetapi=get_api?apiaccesskey=ACCESSKEY
bloodpostapi=post_api
bloodapikey=ACCESSKEY
```

- kotlin ソース内で参照してます
