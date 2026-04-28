/**
 * GrabNGo | University of Mpumalanga 2026
 *
 * Seed script to create a test staff account and initial cafeteria/menu data.
 * Run this using: node scripts/seedTestStaff.js
 */

const admin = require('firebase-admin');
const path = require('path');

// 1. Initialize Firebase Admin SDK
// serviceAccountKey.json must be placed in this folder (scripts/)
const serviceAccountPath = path.join(__dirname, 'serviceAccountKey.json');

try {
    const serviceAccount = require(serviceAccountPath);
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
    });
    console.log('✅ Firebase Admin initialized successfully.');
} catch (error) {
    console.error('❌ Error: serviceAccountKey.json not found in scripts/ folder.');
    console.error('Please download it from Firebase Console -> Project Settings -> Service accounts.');
    process.exit(1);
}

const auth = admin.auth();
const db = admin.firestore();

async function seedData() {
    try {
        const staffEmail = 'staff.test@testump.ac.za';
        const staffPassword = 'StaffTest2026!';

        // 2. Create/Update Firebase Auth User
        let userRecord;
        try {
            userRecord = await auth.getUserByEmail(staffEmail);
            console.log(`ℹ️ User ${staffEmail} already exists. Updating...`);
            await auth.updateUser(userRecord.uid, {
                password: staffPassword,
                displayName: 'Test Staff'
            });
        } catch (error) {
            if (error.code === 'auth/user-not-found') {
                userRecord = await auth.createUser({
                    email: staffEmail,
                    password: staffPassword,
                    displayName: 'Test Staff',
                    emailVerified: true
                });
                console.log(`✅ Created Auth user: ${staffEmail}`);
            } else {
                throw error;
            }
        }

        const uid = userRecord.uid;

        // 3. Write Firestore User Document
        await db.collection('users').document(uid).set({
            userId: uid,
            role: "staff",
            firstName: "Test",
            lastName: "Staff",
            email: staffEmail,
            cafeteriaId: "main-caf",
            isActive: true,
            isVerified: true,
            allergens: [],
            fcmToken: "",
            noShowCount: 0
        });
        console.log(`✅ Written Firestore user document at users/${uid}`);

        // 4. Seed Cafeterias
        const cafeterias = [
            {
                cafeteriaId: "main-caf",
                name: "Main Cafeteria",
                description: "Full home-cooked meals",
                isOpen: true,
                openingTime: "07:30",
                closingTime: "17:00",
                imageUrl: ""
            },
            {
                cafeteriaId: "snack-bar",
                name: "Snack Bar",
                description: "Fast food and snacks",
                isOpen: true,
                openingTime: "08:00",
                closingTime: "16:00",
                imageUrl: ""
            }
        ];

        for (const caf of cafeterias) {
            await db.collection('cafeterias').document(caf.cafeteriaId).set(caf);
            console.log(`✅ Seeded cafeteria: ${caf.cafeteriaId}`);
        }

        // 5. Seed Sample Menu Items for main-caf
        const menuItems = [
            {
                name: "Curry & Rice",
                category: "meals",
                price: 30.0,
                stockCount: 50,
                defaultStockCount: 50,
                isAvailable: true,
                isFeatured: true,
                cafeteriaId: "main-caf",
                allergens: [],
                description: "Hearty beef curry served with white rice.",
                imageUrl: ""
            },
            {
                name: "Pap & Chakalaka",
                category: "meals",
                price: 25.0,
                stockCount: 40,
                defaultStockCount: 40,
                isAvailable: true,
                isFeatured: false,
                cafeteriaId: "main-caf",
                allergens: [],
                description: "Traditional pap with spicy chakalaka.",
                imageUrl: ""
            },
            {
                name: "Coke 500ml",
                category: "drinks",
                price: 15.0,
                stockCount: 100,
                defaultStockCount: 100,
                isAvailable: true,
                isFeatured: false,
                cafeteriaId: "main-caf",
                allergens: [],
                description: "Refreshing 500ml Coca-Cola.",
                imageUrl: ""
            }
        ];

        for (const item of menuItems) {
            // Using a predictable ID based on name for seeding
            const itemId = item.name.toLowerCase().replace(/ /g, '-');
            await db.collection('menuItems').document(itemId).set({
                ...item,
                itemId: itemId
            });
            console.log(`✅ Seeded menu item: ${item.name}`);
        }

        console.log('\n🚀 Seeding completed successfully!');

    } catch (error) {
        console.error('❌ Seeding failed:', error);
    } finally {
        process.exit();
    }
}

seedData();
