# GrabNGo Firebase Seed Scripts

This directory contains scripts to populate your Firebase environment with initial data.

## Setup Instructions

### 1. Download Service Account Key
To interact with Firebase from these scripts, you need a private key file:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Select your project.
3. Click the gear icon (⚙️) -> **Project Settings**.
4. Navigate to the **Service accounts** tab.
5. Click **Generate new private key**.
6. Save the downloaded `.json` file as `serviceAccountKey.json` inside this `scripts/` directory.

> [!CAUTION]
> **Never commit `serviceAccountKey.json` to version control.** It is already added to `.gitignore`.

### 2. Install Dependencies
Run the following command in your terminal at the project root or inside the `scripts/` folder:
```bash
npm install firebase-admin
```

## Running the Seed Script
To create the test staff account, cafeterias, and sample menu items, run:
```bash
node scripts/seedTestStaff.js
```

### Created Test Data:
- **Staff Email**: `staff.test@testump.ac.za`
- **Staff Password**: `StaffTest2026!`
- **Cafeterias**: `main-caf`, `snack-bar`
- **Menu Items**: Curry & Rice, Pap & Chakalaka, Coke 500ml
