@echo off
echo Connexion a Railway MySQL...
"C:\Program Files\MySQL\MySQL Server 9.4\bin\mysql.exe" -h shortline.proxy.rlwy.net -P 26485 -u root -pSDjnYiqCCMFQWlOWeWnhIDFqIaCRTVsr railway < database-setup.sql
echo.
echo Tables creees avec succes !
pause
