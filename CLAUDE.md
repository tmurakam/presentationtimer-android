# CLAUDE.md

このファイルは、このリポジトリで作業する Claude Code (claude.ai/code) に向けたガイダンスです。

## プロジェクト概要

プレゼンタイマ (Presentation Timer) — プレゼン用のカウントアップ/カウントダウンタイマーを表示し、
設定した最大3つの時刻でベル(音+バイブレーション)を鳴らす、シンプルな単一モジュールの Android アプリ (Kotlin)。
デフォルト文字列は英語で、日本語文字列は `values-ja/` にある。

## ビルド & よく使うコマンド

Java 17 が必要 (`.sdkmanrc` 参照、アプリモジュールでは `kotlin { jvmToolchain(17) }`)。

```bash
./gradlew assembleDebug          # デバッグ APK をビルド
./gradlew assembleRelease        # リリース APK をビルド (署名設定が必要、下記参照)
./gradlew build                  # チェックを含むフルビルド
./gradlew test                   # JVM ユニットテスト (モジュール: presentationTimer)
./gradlew connectedAndroidTest   # インストルメンテーションテスト、接続されたデバイス/エミュレータが必要
./gradlew lint                   # Android lint
```

モジュールは `presentationTimer` の1つのみ (namespace `org.tmurakam.presentationtimer`)。ルートの
`build.gradle` はプラグインバージョン (AGP, google-services, Crashlytics) を宣言するだけで、実際の
アプリ設定は `presentationTimer/build.gradle.kts` にある。

現状 `src/test` にテストはなく、`src/androidTest` にはアプリのロジックとは無関係な、ベンダリングした
TimePicker ライブラリ由来の定型的なサンプルインストルメンテーションテストが1つあるだけ。

### リリース署名

`presentationTimer/build.gradle.kts` ではリリース用の `signingConfig` ブロックがコメントアウトされて
いる。署名付きリリースをビルドするには、`presentationTimer/keystore.properties.sample` を
`presentationTimer/keystore.properties` (git 管理外) としてコピーし、`signingConfigs` ブロックと
`signingConfig = signingConfigs.getByName("release")` の行のコメントを解除する。

### Firebase

アプリは Firebase Crashlytics を使用している (`google-services.json` は `presentationTimer/` 配下に
コミットされている)。`FirebaseAnalytics` の利用コードは存在するが `MainActivity` でコメントアウト
されている。

## アーキテクチャ

素の `Activity` ベースのアプリ (Fragment によるナビゲーションなし、Jetpack Compose なし、
ViewModel/DI フレームワークなし)。View Binding を使用 (`buildFeatures.viewBinding = true`)。

- **`MainActivity`** — タイマー画面。`TimerLogic` と `BellRinger` を保持し、UI を直接制御する
  (ボタンラベル、経過時間の閾値に応じたテキスト色、画面点灯維持フラグなど)。カウントアップ/
  カウントダウン表示の切り替え (`mIsCountDown`) は表示上のものであり、内部的には常にゼロから
  カウントアップし続けるタイマー値そのものには影響しない。
- **`TimerLogic`** — Android UI から切り離された純粋なタイマーエンジン。`java.util.Timer` で
  1秒ごとに発火し、`TimerCallback` インターフェース (`onTimerUpdate`) 経由で通知する。
  状態の保存/復元 (`onSaveInstanceState`/`restoreInstanceState`) では経過秒数に加えて壁時計の
  タイムスタンプも保存し、バックグラウンド/回転で失われた時間を補正して経過時間に反映する。
- **`Prefs`** — `SharedPreferences` (`<package>_preferences` ファイル、旧 `PreferenceActivity` の
  デフォルトに準拠) の薄いラッパー。3つのベル時刻(秒単位)、カウントダウンモードで「終了時刻」
  として扱うベル番号、バイブレーション設定の ON/OFF を保持する。
- **`BellRinger`** — 3つの `MediaPlayer` インスタンス (`R.raw.bell1..3`) とバイブレーションパターンを
  保持し、ベル番号 0-2 でインデックスする。
- **`PrefActivity`** — 設定画面。`androidx.preference` ではなく、非推奨の `android.preference.*` API
  (`PreferenceActivity`/`CheckBoxPreference`) をいまだに使用している。各ベル時刻の行から
  `TimeSetActivity` を起動してそのベルの時刻を編集する。
- **`TimeSetActivity`** — ダイアログテーマの Activity。ベンダリングした `TimePicker` (下記参照) を
  ラップして、1つのベルの時:分:秒を編集し、必要に応じてカウントダウンの終了時刻として設定する。
- **`InfoActivity`** — バージョン、ヘルプリンク、プライバシーポリシーリンクなどの About/ヘルプ画面。

### ベンダリングされたサードパーティコード

`com.ikovac.timepickerwithseconds.view.TimePicker`
(`src/main/java/com/ikovac/timepickerwithseconds/` 配下) は、標準の Android `TimePicker` に
秒フィールドを追加するための小さなサードパーティ製 TimePicker ライブラリで、Gradle 依存関係
ではなくソースツリーに直接コピーされている。サードパーティコードとして扱い、無関係なリファクタ
リングは避けること。また `androidTest` に付随する `ExampleInstrumentedTest` は定型的なサンプルで
あり、アプリロジックの実際のテストではない点に注意。

### ベル番号の付け方の慣習

ベル番号は `Prefs` や UI 向け API では 1始まり (`kind`/`i+1`) だが、`BellRinger` では
0始まり (配列インデックス `n`) になっている。呼び出し側 (例: `MainActivity.onTimerOnMainThread`)
での `+1`/`-1` の変換に注意すること。
