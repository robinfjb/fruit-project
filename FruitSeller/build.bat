if not exist release md release

REM android update project -p . 
start /wait /MIN cmd.exe /c "android update project -p . "

REM cd ../FruitLib

REM cd LazyList 
REM android update lib-project -p .
start /wait /MIN cmd.exe /c "android update project -p ../FruitLib "

REM cd ..

start /wait /MIN cmd.exe /c "ant config-main release_mine"