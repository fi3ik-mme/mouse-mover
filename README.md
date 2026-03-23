# Human-Like Mouse Mover
## 📌 What it does

Small Java utility that simulates human-like mouse movement when you are inactive.

## 🎯 Why use it
Prevent system auto-lock / sleep
Avoid “Away” status (Teams, Slack, etc.)
Keep RDP / VPN sessions alive
Useful during long-running tasks or demos
## ⚙️ How it works
Checks mouse position every second
If you move → does nothing
If idle for 60s → performs smooth random movement
## 🚀 Run
to build:
`mvn clean package`
to run binary:
`java -jar target/mouse-mover-1.0.0.jar`
or just download and run mouse-mover-1.0.0.jar
## 🧠 Usage
Start the app
Leave it running in background
It will auto-move mouse only when you’re idle

Stop with:

CTRL + C
## ⚙️ Config (in code)
Idle timeout → 60_000
Check interval → 1000
Movement smoothness → 20–50 steps
## ⚠️ Notes
Does not click or type (only moves mouse)
Use responsibly (company policies may apply)