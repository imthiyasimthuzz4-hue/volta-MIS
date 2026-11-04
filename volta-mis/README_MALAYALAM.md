# VOLTA MIS — അപ്ഡേറ്റ് ചെയ്ത പ്രോജക്ട്

ഈ പതിപ്പിൽ നിലവിലുള്ള Dashboard, Customers, Projects എന്നിവ നിലനിർത്തി താഴെപ്പറയുന്ന പ്രവർത്തനങ്ങൾ ചേർത്തിട്ടുണ്ട്:
- Staff: ജീവനക്കാരുടെ വിവരങ്ങൾ ചേർക്കുക, ലിസ്റ്റ് കാണുക, നീക്കം ചെയ്യുക
- Materials: സാധനങ്ങളുടെ കോഡ്, യൂണിറ്റ്, സ്റ്റോക്ക് അളവ്, വില, വിതരണക്കാരൻ എന്നിവ രേഖപ്പെടുത്തുക
- Expenses: ചെലവ്, വിഭാഗം, തീയതി, പേയ്‌മെന്റ് മാർഗം എന്നിവ രേഖപ്പെടുത്തുക
- Reports: റെക്കോർഡ് എണ്ണങ്ങളും ചെലവുകളുടെ മൊത്തവും കാണുക; Print / Save PDF
- `run.bat`: പ്രോജക്ട് ഫോൾഡറിൽ നിന്ന് ലോഞ്ച് ചെയ്യുകയും ബ്രൗസർ തുറക്കുകയും ചെയ്യും
- `install-autostart.bat`: Windows sign-in സമയത്ത് ലോഞ്ചർ ആരംഭിക്കാൻ ഓപ്ഷൻ നൽകുന്നു

## ആരംഭിക്കുന്ന വിധം
1. Java 21, Apache Maven 3.9+, MySQL 8 എന്നിവ ഇൻസ്റ്റാൾ ചെയ്തിരിക്കണം.
2. MySQL സർവർ പ്രവർത്തിക്കുന്നുണ്ടെന്ന് ഉറപ്പാക്കുക.
3. `volta_mis` ഡാറ്റാബേസിനായി MySQL `root` പാസ്‌വേഡ് തയ്യാറാക്കുക.
4. `pom.xml` ഉള്ള ഈ ഫോൾഡറിൽ `run.bat` ഡബിൾ ക്ലിക്ക് ചെയ്യുക.
5. MySQL root password ചോദിക്കുമ്പോൾ പാസ്‌വേഡ് നൽകുക. കറുത്ത വിൻഡോ തുറന്നുവെക്കണം.
6. ബ്രൗസറിൽ `http://localhost:8080/login` തുറക്കും.

## Windows login-ൽ ഓട്ടോ-സ്റ്റാർട്ട്
`install-autostart.bat` ഡബിൾ ക്ലിക്ക് ചെയ്യുക. Windows-ൽ sign in ചെയ്യുമ്പോൾ launcher ആരംഭിക്കും; സുരക്ഷിതത്വത്തിനായി MySQL password വീണ്ടും ചോദിക്കും. ഇത് unattended service അല്ല.

## പ്രധാന കുറിപ്പുകൾ
- ഡാറ്റ `volta_mis` MySQL ഡാറ്റാബേസിലാണ് സൂക്ഷിക്കുന്നത്. പഴയ ഡാറ്റാബേസ്/ഫോൾഡർ ഡിലീറ്റ് ചെയ്യരുത്.
- `application.properties`-ൽ പാസ്‌വേഡ് hard-code ചെയ്യാതെ environment variable `DB_PASSWORD` ആണ് ഉപയോഗിക്കുന്നത്.
- ഈ അപ്ഡേറ്റ് ഒരു വികസന പതിപ്പാണ്. യഥാർത്ഥ ബിസിനസ് ഉപയോഗത്തിന് മുമ്പ് backup/restore, role permissions, input validation, stock movement history, salary/privacy controls, automated tests എന്നിവ പരിശോധിക്കുകയും വേണം.
- നിലവിലുള്ള `root` അക്കൗണ്ട് ഉപയോഗിക്കുന്നത് താൽക്കാലികമായ local setup-നായി മാത്രം ശുപാർശ ചെയ്യുന്നു; dedicated least-privilege MySQL user സൃഷ്ടിക്കുക.
