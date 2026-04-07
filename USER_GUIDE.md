# CivicTrack – User guide (JavaFX demo)

This guide matches the **in-app User guide** (classpath: `com/urbanissue/text/user-guide.txt`). **MySQL** must be running and **`schema.sql`** applied before you use the app.

## Demo users (Citizen, Official, Admin)

Load once after schema:

```bash
mysql -u root -p < seed-demo.sql
```

| Role | Email | Password |
|------|-------|----------|
| **Citizen** | `citizen@civictrack.local` | `pass123` |
| **Official** | `official@civictrack.local` | `pass123` |
| **Admin** | `admin@civictrack.local` | `pass123` |

**Self-registration:** **Create Account** always registers you as a **Citizen**. Official and Admin accounts are provided by your organisation (e.g. `seed-demo.sql`) or future admin tools—not chosen on the public sign-up form.

**Built-in super admin (codebase):**
- Email: `superadmin@civictrack.local`
- Password: `superadmin123`

Use this account to sign in and approve/promote users from **Admin → User Management → Approve/Promote Admin**.

**Welcome screen:** opens on **Landing** (two panels: **For residents** and **For MCAs & ward admins**). **Learn more** loads `learn-more-citizens.txt` and `learn-more-mcas-ward-admins.txt` from the classpath. **User guide** is on **Register** and on **role dashboards** (not on Login). **← Back to welcome** returns to Landing from Sign in.

---

## Before you start (Linux)

1. **Go to this project folder** (the one that contains `pom.xml` and `schema.sql`):

   ```bash
   cd /path/to/CivicTrack/oop-java-project
   ```

   If you see `No such file`, you are in the wrong directory. Check with:

   ```bash
   ls
   ```

   You should see `pom.xml`, `schema.sql`, and `src/`.

2. **Create the database** (run from the same folder so `schema.sql` is found):

   ```bash
   sudo mysql -u root -p < schema.sql
   sudo mysql -u root -p < seed-demo.sql
   ```

   (`seed-demo.sql` adds Citizen, Official, and Admin demo users plus a sample issue.)

   Or, from anywhere, use the **full path** to `schema.sql`:

   ```bash
   sudo mysql -u root -p < /full/path/to/oop-java-project/schema.sql
   ```

3. **Match MySQL credentials** in `src/main/java/com/urbanissue/db/DBConfig.java` (`USERNAME`, `PASSWORD`, and `URL` if needed).

4. **Start the app**:

   ```bash
   ./scripts/run-javafx-linux.sh
   ```

   Or:

   ```bash
   mvn clean compile javafx:run
   ```

---

## 1. User registration (self-service)

1. On the **Login** screen, open **Create Account**.
2. Enter **name**, **email**, **password**, **phone** (optional).
3. Submit — you are saved as a **Citizen** and can sign in immediately after.
4. **Official** and **Admin** accounts are not created through this form; use `seed-demo.sql` or your organisation’s process.

---

## 2. Login and role-based dashboards

1. Enter **email** and **password**, then **Login**.
2. After login you are sent to the dashboard for your role:
   - **Citizen** → citizen dashboard (my issues, report issue).
   - **Official** → official dashboard (assigned issues).
   - **Admin** → admin dashboard (all issues, assign, reports).

---

## 3. Report an issue (Citizen)

1. Log in as a **Citizen**.
2. Open **Report issue** (or similar).
3. Fill **title**, **description**, **location**, choose **category** and **priority**.
4. Optionally use **Choose file** / upload for an **image** (saved as an attachment path).
5. Submit. The issue is stored in MySQL.

---

## 4. View reported issues

| Role      | What you see |
|-----------|----------------|
| **Citizen** | **My issues** – only issues you reported. |
| **Official** | **Assigned** – issues assigned to you. |
| **Admin**   | **All issues** – every issue in the system. |

Use the table and **View detail** (or double-click, depending on UI) to open an issue.

---

## 5. Assign issue to official (Admin)

1. Log in as **Admin**.
2. In the issues table, select an issue.
3. Choose an **official** from the combo box.
4. Click **Assign** (or equivalent). The issue’s `assigned_official` is updated in the database.

---

## 6. Update issue status (Official)

1. Log in as an **Official** who has **assigned** issues (use Admin to assign first).
2. Select an issue and change **status** to **IN_PROGRESS** or **RESOLVED** (from **PENDING**), then save/update as the UI provides.

---

## 7. Comments on an issue

1. Open **issue detail** (from Citizen/Official/Admin flow as available).
2. Enter comment text and **Add comment** (or similar).
3. Comments are stored and shown for that issue.

---

## 8. Reports (Admin – by status and priority)

1. Log in as **Admin**.
2. Open **Reports** (from the admin dashboard).
3. Review summaries **by status** and **by priority** as shown on screen.

---

## Quick demo script (for a client or marker)

1. Use **demo users** from `seed-demo.sql` for Official and Admin; **Create Account** for a new Citizen (or use demo Citizen).
2. **Citizen**: report an issue (with optional image).
3. **Admin**: open all issues, **assign** the new issue to the Official.
4. **Official**: open assigned issues, set status to **IN_PROGRESS**, then **RESOLVED**.
5. **Citizen**: open **My issues** and confirm status updated.
6. **Anyone with access**: open issue **detail**, add a **comment**.
7. **Admin**: open **Reports** and show status/priority breakdown.

---

## Troubleshooting

| Problem | What to check |
|--------|----------------|
| `No such file` when loading schema | Run `mysql ... < schema.sql` from the folder that **contains** `schema.sql`, or use the full path to `schema.sql`. |
| `/usr/bin/env: 'bash\r': No such file or directory` | The script was saved with Windows (CRLF) line endings. From the repo: pull the latest `scripts/run-javafx-linux.sh`, or run `sed -i 's/\r$//' scripts/run-javafx-linux.sh` (or `dos2unix scripts/run-javafx-linux.sh`). |
| `mvn: command not found` | Install Maven: `sudo apt install maven` |
| JavaFX window does not open (Linux) | Display required: WSLg or X11. Check `echo $DISPLAY`. |
| Database errors on login | MySQL running? `DBConfig.java` matches your DB user/password? |
