# VOLTA MIS — ആദ്യ പതിപ്പ്

ഈ പ്രോജക്ട് Java 21, Spring Boot, MySQL എന്നിവ ഉപയോഗിക്കുന്നു. ഇത് **ആദ്യ പ്രവർത്തന അടിത്തറയാണ്**: സുരക്ഷിതമായ login, dashboard, customers, projects. Staff, Materials, Expenses, Reports മെനുകൾ ഇപ്പോൾ placeholder മാത്രമാണ്; അവയ്ക്ക് യഥാർത്ഥ പ്രവർത്തനങ്ങൾ ചേർക്കണം.

## ആവശ്യമായവ
- Java 21
- Maven 3.9+
- MySQL Server 8.x
- VS Code + Extension Pack for Java

## 1. MySQL തയ്യാറാക്കുക
MySQL-ൽ താഴെയുള്ള SQL പ്രവർത്തിപ്പിക്കുക:

```sql
CREATE DATABASE IF NOT EXISTS volta_mis;
```

പഴയ ഡാറ്റാബേസ് ഇതിനകം ഉണ്ടെങ്കിൽ അത് delete ചെയ്യരുത്.

## 2. ഡാറ്റാബേസ് password ക്രമീകരിക്കുക
`src/main/resources/application.properties` തുറന്ന് `CHANGE_THIS_PASSWORD` എന്നിടത്ത് നിങ്ങളുടെ MySQL root password നൽകുക. യഥാർത്ഥ password മറ്റൊരാൾക്ക് അയക്കരുത്. കൂടുതൽ സുരക്ഷയ്ക്കായി പിന്നീട് പ്രത്യേകം database user സൃഷ്ടിക്കുക.

## 3. പ്രോജക്ട് പ്രവർത്തിപ്പിക്കുക
ZIP extract ചെയ്ത് `volta-mis` ഫോൾഡർ VS Code-ൽ തുറക്കുക. VS Code Terminal തുറന്ന്:

```bash
mvn spring-boot:run
```

ആദ്യ തവണ Maven ആവശ്യമായ dependencies ഇന്റർനെറ്റിൽ നിന്ന് ഡൗൺലോഡ് ചെയ്യും. Maven command ലഭ്യമല്ലെങ്കിൽ Maven ഇൻസ്റ്റാൾ ചെയ്യണം.

## 4. തുറക്കുക
Browser-ൽ `http://localhost:8080` തുറക്കുക.

- Username: `admin`
- Initial password: `Admin@12345`

ആദ്യ login കഴിഞ്ഞ് ഈ password മാറ്റുന്നതുവരെ യഥാർത്ഥ ബിസിനസ് ഡാറ്റ ഉപയോഗിക്കരുത്. ഇപ്പോഴത്തെ പതിപ്പിൽ password മാറ്റാനുള്ള പേജ് ഉൾപ്പെടുത്തിയിട്ടില്ല; അതിനാൽ ഇത് local development/demo-യ്ക്ക് മാത്രം ഉപയോഗിക്കുക.

## ശ്രദ്ധിക്കുക
- `app_users` എന്ന വേറിട്ട പട്ടികയിലാണ് ഈ പതിപ്പിന്റെ login users. പഴയ `users` പട്ടികയുമായി ഇത് കൂട്ടിക്കലർത്തുന്നില്ല.
- Hibernate `ddl-auto=update` ആണ്. Database migration, backups, role permissions, password-change screen, audit logs, tests എന്നിവ ചേർത്തശേഷം മാത്രമേ production-ൽ ഉപയോഗിക്കാവൂ.
- ഇത് ഔദ്യോഗിക production-ready സിസ്റ്റമല്ല; വികസിപ്പിക്കാനുള്ള starter project ആണ്.


## അപ്‌ഡേറ്റ്: Staff, Materials, Expenses, Reports
ഈ പതിപ്പിൽ Staff, Materials, Expenses എന്നീ മോഡ്യൂളുകൾക്ക് ചേർക്കൽ, പട്ടിക, തിരുത്തൽ, ഡിലീറ്റ് സൗകര്യങ്ങളും Reports പേജിൽ സംഗ്രഹവും പ്രിന്റ് സൗകര്യവും ചേർത്തിട്ടുണ്ട്.
അപ്‌ഡേറ്റ് ചെയ്യുന്നതിന് മുമ്പ് നിലവിലുള്ള ഫോൾഡറിന്റെ ബാക്കപ്പ് എടുക്കുക. നിങ്ങളുടെ MySQL ഡാറ്റാബേസ് `volta_mis` മാറ്റേണ്ടതില്ല. പുതിയ entity-കൾക്കായി Hibernate `ddl-auto=update` വഴി പുതിയ പട്ടികകൾ സൃഷ്ടിക്കും. നിലവിലുള്ള server നിർത്തി ഈ ZIP-യിലെ പ്രോജക്ട് ഫയലുകൾ ഉപയോഗിച്ച് `run.bat` ആരംഭിക്കുക.
