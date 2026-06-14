# ☕ Brew & Blend — Coffee Shop Management System

একটি সম্পূর্ণ **Java Swing** ভিত্তিক ডেস্কটপ অ্যাপ্লিকেশন যা কফি শপ পরিচালনার জন্য তৈরি। এটি **Customer** এবং **Admin** উভয় ধরনের ব্যবহারকারীর জন্য আলাদা Dashboard সরবরাহ করে, MySQL ডেটাবেজের সাথে সম্পূর্ণ ইন্টিগ্রেটেড।

---

## 📋 বিষয়সূচি

- [ফিচারসমূহ](#-ফিচারসমূহ)
- [প্রযুক্তি স্ট্যাক](#-প্রযুক্তি-স্ট্যাক)
- [পূর্বশর্ত](#-পূর্বশর্ত)
- [ইনস্টলেশন ও সেটআপ](#-ইনস্টলেশন-ও-সেটআপ)
- [প্রজেক্ট স্ট্রাকচার](#-প্রজেক্ট-স্ট্রাকচার)
- [ডিফল্ট লগইন তথ্য](#-ডিফল্ট-লগইন-তথ্য)
- [ডেটাবেজ স্কিমা](#-ডেটাবেজ-স্কিমা)
- [স্ক্রিনশট](#-স্ক্রিনশট)
- [লাইসেন্স](#-লাইসেন্স)

---

## ✨ ফিচারসমূহ

### 🔐 Authentication (লগইন ও রেজিস্ট্রেশন)
- ইমেইল ও পাসওয়ার্ড দিয়ে **লগইন**
- নতুন অ্যাকাউন্ট **রেজিস্ট্রেশন** (Customer / Admin রোল নির্বাচন)
- অ্যানিমেটেড স্টিম ইফেক্টসহ প্রিমিয়াম লগইন UI

### 👤 Customer Dashboard
- **মেনু ব্রাউজ করা** — সুন্দর কার্ড ভিউতে কফি ও ফুড আইটেম দেখা
- **মেনু সার্চ** — নাম দিয়ে দ্রুত আইটেম খুঁজে বের করা
- **অর্ডার দেওয়া** — ডিসকাউন্ট কোড সহ অর্ডার প্লেস করা
- **অর্ডার ট্র্যাকিং** — নিজের অর্ডারের স্ট্যাটাস দেখা (Pending → Preparing → Delivered)
- **ফিডব্যাক দেওয়া** — ১-৫ রেটিং ও মন্তব্য জমা দেওয়া

### 🛡️ Admin Dashboard
- **মেনু ম্যানেজমেন্ট** — নতুন আইটেম যোগ, এডিট ও ডিলিট করা (CRUD)
- **অর্ডার ম্যানেজমেন্ট** — সকল অর্ডার দেখা ও স্ট্যাটাস আপডেট করা
- **ফিডব্যাক রিভিউ** — সকল কাস্টমার ফিডব্যাক দেখা
- **সেলস রিপোর্ট** — মোট বিক্রি, গড় অর্ডার মূল্য, জনপ্রিয় আইটেম ও আইটেমভিত্তিক ব্রেকডাউন

### 🎨 UI/UX
- **Nimbus Look & Feel** সহ আধুনিক ডিজাইন
- কাস্টম **Theme Engine** — গ্র্যাডিয়েন্ট বাটন, শ্যাডো কার্ড, অ্যানিমেটেড নেভবার
- **হোভার অ্যানিমেশন** সহ ইন্টারেক্টিভ কার্ড
- প্রিমিয়াম কফি-থিমড কালার প্যালেট (Espresso Black, Warm Cream, Gold Accent)
- কাস্টম স্টাইলড টেক্সট ফিল্ড প্লেসহোল্ডার সাপোর্ট সহ

---

## 🛠️ প্রযুক্তি স্ট্যাক

| প্রযুক্তি | বিবরণ |
|---|---|
| **Java (JDK 8+)** | মূল প্রোগ্রামিং ভাষা |
| **Java Swing** | GUI ফ্রেমওয়ার্ক |
| **MySQL** | রিলেশনাল ডেটাবেজ |
| **MySQL Connector/J 8.3.0** | JDBC ড্রাইভার |
| **Nimbus L&F** | মডার্ন লুক অ্যান্ড ফিল |

---

## 📌 পূর্বশর্ত

এই প্রজেক্ট চালানোর আগে নিশ্চিত করুন আপনার সিস্টেমে নিম্নলিখিত সফটওয়্যার ইনস্টল আছে:

1. **Java Development Kit (JDK)** — সংস্করণ 8 বা তার উপরে
   ```
   java -version
   javac -version
   ```

2. **MySQL Server** — সংস্করণ 5.7 বা তার উপরে
   - MySQL সার্ভার চালু থাকতে হবে `localhost:3306` পোর্টে
   - ডিফল্ট ব্যবহারকারী: `root`
   - ডিফল্ট পাসওয়ার্ড: `12345`

> [!IMPORTANT]
> আপনার MySQL পাসওয়ার্ড যদি `12345` না হয়, তাহলে `src/com/coffeeshop/database/DatabaseManager.java` ফাইলের **১৯ নম্বর লাইনে** `DB_PASSWORD` পরিবর্তন করুন।

---

## 🚀 ইনস্টলেশন ও সেটআপ

### ১. রিপোজিটরি ক্লোন করুন
```bash
git clone https://github.com/TauhidOSD/CoffeShop.git
cd CoffeShop
```

### ২. MySQL সার্ভার চালু করুন
MySQL সার্ভার চালু আছে কিনা নিশ্চিত করুন। ডেটাবেজ ও টেবিল স্বয়ংক্রিয়ভাবে তৈরি হবে।

### ৩. অ্যাপ্লিকেশন চালান

**Windows এ (Batch Script):**
```bash
run.bat
```

**ম্যানুয়ালি চালাতে:**
```bash
# ১. কম্পাইল করুন
mkdir bin
javac -cp "lib/*" -d bin src/com/coffeeshop/**/*.java src/com/coffeeshop/Main.java

# ২. রান করুন
java -cp "bin;lib/*" com.coffeeshop.Main
```

> [!NOTE]
> Linux/macOS এ classpath separator হিসেবে `;` এর বদলে `:` ব্যবহার করুন:
> ```bash
> java -cp "bin:lib/*" com.coffeeshop.Main
> ```

---

## 📁 প্রজেক্ট স্ট্রাকচার

```
coffeShop/
├── assets/                          # কফি আইটেমের ইমেজ ফাইল
│   ├── americano.png
│   ├── cappuccino.png
│   ├── caramel_macchiato.png
│   ├── cold_brew.png
│   ├── croissant.png
│   ├── espresso.png
│   ├── latte.png
│   ├── mocha.png
│   └── muffin.png
├── lib/                             # এক্সটার্নাল লাইব্রেরি
│   └── mysql-connector-j-8.3.0.jar
├── src/                             # সোর্স কোড
│   └── com/coffeeshop/
│       ├── Main.java                # অ্যাপ্লিকেশন এন্ট্রি পয়েন্ট
│       ├── database/
│       │   └── DatabaseManager.java # ডেটাবেজ CRUD অপারেশন
│       ├── model/                   # ডেটা মডেল ক্লাস
│       │   ├── Feedback.java
│       │   ├── MenuItem.java
│       │   ├── Order.java
│       │   └── User.java
│       └── ui/                      # ইউজার ইন্টারফেস
│           ├── AdminDashboard.java
│           ├── CustomerDashboard.java
│           ├── LoginRegisterWindow.java
│           └── Theme.java           # কাস্টম থিম ইঞ্জিন
├── bin/                             # কম্পাইলড ক্লাস ফাইল (auto-generated)
├── run.bat                          # Windows লঞ্চ স্ক্রিপ্ট
└── README.md
```

---

## 🔑 ডিফল্ট লগইন তথ্য

অ্যাপ্লিকেশন প্রথমবার চালু হলে নিচের ডিফল্ট ইউজার তৈরি হয়:

| রোল | নাম | ইমেইল | পাসওয়ার্ড |
|---|---|---|---|
| **Admin** | Admin User | `admin@coffeeshop.com` | `admin123` |
| **Customer** | John Doe | `john@gmail.com` | `john123` |

---

## 🗄️ ডেটাবেজ স্কিমা

অ্যাপ্লিকেশন `coffeeshop_db` নামে একটি MySQL ডেটাবেজ তৈরি করে, যেখানে ৪টি টেবিল থাকে:

```
┌─────────────┐       ┌─────────────┐
│   users      │       │    menu      │
├─────────────┤       ├─────────────┤
│ id (PK)      │       │ id (PK)      │
│ name         │       │ item_name    │
│ email (UQ)   │       │ price        │
│ password     │       └──────┬───────┘
│ role         │              │
└──────┬───────┘              │
       │                      │
       │    ┌─────────────────┘
       │    │
       ▼    ▼
┌─────────────────┐
│     orders       │
├─────────────────┤
│ id (PK)          │
│ user_id (FK)     │
│ item_id (FK)     │
│ status           │
│ discount_code    │
│ final_price      │
│ order_date       │
└─────────────────┘

┌─────────────────┐
│    feedback      │
├─────────────────┤
│ id (PK)          │
│ user_id (FK)     │
│ rating (1-5)     │
│ comments         │
│ feedback_date    │
└─────────────────┘
```

### ডিফল্ট মেনু আইটেম

| আইটেম | মূল্য ($) |
|---|---|
| Espresso | 2.50 |
| Cappuccino | 3.50 |
| Caffè Latte | 3.80 |
| Caramel Macchiato | 4.20 |
| Americano | 3.00 |
| Mocha | 4.50 |
| Cold Brew | 3.50 |
| Chocolate Croissant | 3.00 |
| Blueberry Muffin | 2.80 |

---

## 📸 স্ক্রিনশট

> অ্যাপ্লিকেশন চালানোর পর লগইন পেজ, কাস্টমার ড্যাশবোর্ড এবং অ্যাডমিন ড্যাশবোর্ডের স্ক্রিনশট এখানে যোগ করতে পারেন।

---

## ⚙️ কনফিগারেশন

ডেটাবেজ সেটিংস পরিবর্তন করতে `DatabaseManager.java` ফাইল এডিট করুন:

```java
private static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "coffeeshop_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "12345";  // আপনার পাসওয়ার্ড দিন
```

---

## 🤝 কন্ট্রিবিউশন

কন্ট্রিবিউট করতে চাইলে:

1. প্রজেক্ট **Fork** করুন
2. আপনার ফিচার ব্রাঞ্চ তৈরি করুন (`git checkout -b feature/amazing-feature`)
3. পরিবর্তন **Commit** করুন (`git commit -m 'Add amazing feature'`)
4. ব্রাঞ্চে **Push** করুন (`git push origin feature/amazing-feature`)
5. একটি **Pull Request** খুলুন

---

## 📄 লাইসেন্স

এই প্রজেক্টটি শিক্ষামূলক উদ্দেশ্যে তৈরি।

---

<p align="center">
  তৈরি করেছেন ❤️ দিয়ে — <strong>Brew & Blend Coffee Shop</strong>
</p>
