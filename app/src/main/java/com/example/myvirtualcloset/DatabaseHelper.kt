package com.example.mvc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class Designer(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val uniqueId: String
)

data class Design(
    val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageFile: String,
    val designerId: Long
)

data class ClothingItem(
    val id: Long,
    val color: String,
    val category: String,
    val imagePath: String
) {
    override fun toString(): String {
        return "$color $category"
    }
}

data class User(
    val id: Long,
    val name: String,
    val email: String
)



class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MVCDatabase"
        private const val DATABASE_VERSION = 3 // Increased version for proper updates

        // Enthusiast (Users) table
        const val TABLE_NAME = "Enthusiast"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // Clothing table
        const val CLOTHING_TABLE_NAME = "Clothing"
        const val COLUMN_CLOTHING_ID = "id"
        const val COLUMN_CLOTHING_COLOR = "color"
        const val COLUMN_CLOTHING_CATEGORY = "category"
        const val COLUMN_CLOTHING_IMAGE = "image"

        // Designer table
        const val DESIGNER_TABLE_NAME = "Designer"
        const val COLUMN_DESIGNER_ID = "id"
        const val COLUMN_DESIGNER_NAME = "name"
        const val COLUMN_DESIGNER_EMAIL = "email"
        const val COLUMN_DESIGNER_PASSWORD = "password"
        const val COLUMN_DESIGNER_UNIQUE_ID = "unique_id"

        // Design table constants
        const val DESIGN_TABLE_NAME = "Design"
        const val COLUMN_DESIGN_ID = "id"
        const val COLUMN_DESIGN_TITLE = "title"
        const val COLUMN_DESIGN_DESCRIPTION = "description"
        const val COLUMN_DESIGN_PRICE = "price"
        const val COLUMN_DESIGN_CATEGORY = "category"
        const val COLUMN_DESIGN_IMAGE_FILE = "image_file"
        const val COLUMN_DESIGN_DESIGNER_ID = "designer_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1) Enthusiast (Users)
        val createEnthusiastTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createEnthusiastTable)

        // 2) Clothing
        val createClothingTable = """
            CREATE TABLE $CLOTHING_TABLE_NAME (
                $COLUMN_CLOTHING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLOTHING_COLOR TEXT NOT NULL,
                $COLUMN_CLOTHING_CATEGORY TEXT NOT NULL,
                $COLUMN_CLOTHING_IMAGE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createClothingTable)

        // 3) Designer
        val createDesignerTable = """
            CREATE TABLE $DESIGNER_TABLE_NAME (
                $COLUMN_DESIGNER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESIGNER_NAME TEXT NOT NULL,
                $COLUMN_DESIGNER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_DESIGNER_PASSWORD TEXT NOT NULL,
                $COLUMN_DESIGNER_UNIQUE_ID TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
        db.execSQL(createDesignerTable)

        // 4) Design table
        val createDesignTable = """
            CREATE TABLE $DESIGN_TABLE_NAME (
                $COLUMN_DESIGN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESIGN_TITLE TEXT NOT NULL,
                $COLUMN_DESIGN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_DESIGN_PRICE REAL NOT NULL,
                $COLUMN_DESIGN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESIGN_IMAGE_FILE TEXT NOT NULL,
                $COLUMN_DESIGN_DESIGNER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_DESIGN_DESIGNER_ID) REFERENCES $DESIGNER_TABLE_NAME($COLUMN_DESIGNER_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createDesignTable)

        // 5) Favorites (Clothing)
        val createFavoritesTable = """
            CREATE TABLE Favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                clothing_id INTEGER,
                user_email TEXT,
                favorited_at INTEGER NOT NULL,
                FOREIGN KEY (clothing_id) REFERENCES $CLOTHING_TABLE_NAME($COLUMN_CLOTHING_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createFavoritesTable)

        // 6) Outfit
        val createOutfitTable = """
            CREATE TABLE Outfit (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                season TEXT NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createOutfitTable)

        // 7) OutfitItems
        val createOutfitItemsTable = """
            CREATE TABLE OutfitItems (
                outfit_id INTEGER,
                clothing_id INTEGER,
                PRIMARY KEY (outfit_id, clothing_id),
                FOREIGN KEY (outfit_id) REFERENCES Outfit(id) ON DELETE CASCADE,
                FOREIGN KEY (clothing_id) REFERENCES $CLOTHING_TABLE_NAME($COLUMN_CLOTHING_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createOutfitItemsTable)

        // 8) FavoriteOutfits
        val createFavoriteOutfitsTable = """
            CREATE TABLE FavoriteOutfits (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                outfit_id INTEGER,
                user_email TEXT,
                favorited_at INTEGER NOT NULL,
                FOREIGN KEY (outfit_id) REFERENCES Outfit(id) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createFavoriteOutfitsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop all tables in correct order to avoid foreign key constraints
        db.execSQL("DROP TABLE IF EXISTS FavoriteOutfits")
        db.execSQL("DROP TABLE IF EXISTS Favorites")
        db.execSQL("DROP TABLE IF EXISTS OutfitItems")
        db.execSQL("DROP TABLE IF EXISTS Outfit")
        db.execSQL("DROP TABLE IF EXISTS $DESIGN_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CLOTHING_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $DESIGNER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // ============== USER METHODS ==============
    fun insertUser(username: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return try {
            db.insertOrThrow(TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting user", e)
            -1L
        }
    }

    fun validateUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        return try {
            val projection = arrayOf(COLUMN_EMAIL)
            val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
            val selectionArgs = arrayOf(email, password)

            db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            ).use { cursor ->
                cursor.count > 0
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error validating user", e)
            false
        }
    }

    fun updateUser(username: String, email: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
        }
        val selection = "$COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        return db.update(TABLE_NAME, values, selection, selectionArgs)
    }

    fun getCurrentUser(email: String): Pair<String, String>? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_USERNAME, $COLUMN_EMAIL FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?"

        return db.rawQuery(query, arrayOf(email)).use { cursor ->
            if (cursor.moveToFirst()) {
                Pair(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
            } else {
                null
            }
        }
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"

        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                users.add(User(id, name, email))
            }
        }
        return users
    }

    fun deleteUser(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // ============== DESIGNER METHODS ==============
    fun insertDesigner(name: String, email: String, password: String, uniqueId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESIGNER_NAME, name)
            put(COLUMN_DESIGNER_EMAIL, email)
            put(COLUMN_DESIGNER_PASSWORD, password)
            put(COLUMN_DESIGNER_UNIQUE_ID, uniqueId)
        }
        return try {
            db.insertOrThrow(DESIGNER_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting designer", e)
            -1L
        }
    }

    fun updateDesignerWithPassword(id: Long, name: String, email: String, uniqueId: String, password: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESIGNER_NAME, name)
            put(COLUMN_DESIGNER_EMAIL, email)
            put(COLUMN_DESIGNER_UNIQUE_ID, uniqueId)
            put(COLUMN_DESIGNER_PASSWORD, password)
        }
        return db.update(
            DESIGNER_TABLE_NAME,
            values,
            "$COLUMN_DESIGNER_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun validateDesigner(email: String, password: String): Boolean {
        val db = this.readableDatabase
        return try {
            val projection = arrayOf(COLUMN_DESIGNER_EMAIL)
            val selection = "$COLUMN_DESIGNER_EMAIL = ? AND $COLUMN_DESIGNER_PASSWORD = ?"
            val selectionArgs = arrayOf(email, password)

            db.query(
                DESIGNER_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            ).use { cursor ->
                cursor.count > 0
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error validating designer", e)
            false
        }
    }

    fun getAllDesigners(): List<Designer> {
        val designers = mutableListOf<Designer>()
        val db = readableDatabase
        val query = "SELECT * FROM $DESIGNER_TABLE_NAME"

        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_PASSWORD))
                val uniqueId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_UNIQUE_ID))
                designers.add(Designer(id, name, email, password, uniqueId))
            }
        }
        return designers
    }

    fun getDesignerByEmail(email: String): Designer? {
        val db = readableDatabase
        val query = "SELECT * FROM $DESIGNER_TABLE_NAME WHERE $COLUMN_DESIGNER_EMAIL = ?"

        return db.rawQuery(query, arrayOf(email)).use { cursor ->
            if (cursor.moveToFirst()) {
                Designer(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_NAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_EMAIL)),
                    password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_PASSWORD)),
                    uniqueId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_UNIQUE_ID))
                )
            } else {
                null
            }
        }
    }

    // Fixed: Method to get designer by ID - now includes uniqueId
    fun getDesignerById(designerId: Int): Designer? {
        val db = this.readableDatabase
        val cursor = db.query(
            DESIGNER_TABLE_NAME,
            null,
            "$COLUMN_DESIGNER_ID = ?",
            arrayOf(designerId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            Designer(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_PASSWORD)),
                uniqueId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGNER_UNIQUE_ID))
            )
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    // Updated method to include uniqueId parameter
    fun updateDesigner(id: Long, name: String, email: String, uniqueId: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESIGNER_NAME, name)
            put(COLUMN_DESIGNER_EMAIL, email)
            put(COLUMN_DESIGNER_UNIQUE_ID, uniqueId)
        }
        return db.update(
            DESIGNER_TABLE_NAME,
            values,
            "$COLUMN_DESIGNER_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteDesigner(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            DESIGNER_TABLE_NAME,
            "$COLUMN_DESIGNER_ID = ?",
            arrayOf(id.toString())
        )
    }

    // ============== DESIGN METHODS ==============
    fun addDesign(
        designerId: Long,
        title: String,
        description: String,
        price: Double,
        category: String,
        imageFileName: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESIGN_DESIGNER_ID, designerId)
            put(COLUMN_DESIGN_TITLE, title)
            put(COLUMN_DESIGN_DESCRIPTION, description)
            put(COLUMN_DESIGN_PRICE, price)
            put(COLUMN_DESIGN_CATEGORY, category)
            put(COLUMN_DESIGN_IMAGE_FILE, imageFileName)
        }
        return try {
            db.insertOrThrow(DESIGN_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding design", e)
            -1L
        }
    }

    fun getDesignsByDesigner(designerId: Long): List<Design> {
        val db = readableDatabase
        val designs = mutableListOf<Design>()
        val query = "SELECT * FROM $DESIGN_TABLE_NAME WHERE $COLUMN_DESIGN_DESIGNER_ID = ?"

        db.rawQuery(query, arrayOf(designerId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                designs.add(
                    Design(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_TITLE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_DESCRIPTION)),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_PRICE)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_CATEGORY)),
                        imageFile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_IMAGE_FILE)),
                        designerId = designerId
                    )
                )
            }
        }
        return designs
    }

    fun getAllDesigns(): List<Design> {
        val db = readableDatabase
        val designs = mutableListOf<Design>()
        val query = "SELECT * FROM $DESIGN_TABLE_NAME"

        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                designs.add(
                    Design(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_TITLE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_DESCRIPTION)),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_PRICE)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_CATEGORY)),
                        imageFile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_IMAGE_FILE)),
                        designerId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DESIGN_DESIGNER_ID))
                    )
                )
            }
        }
        return designs
    }

    fun deleteDesign(designId: Long): Int {
        val db = writableDatabase
        return db.delete(DESIGN_TABLE_NAME, "$COLUMN_DESIGN_ID = ?", arrayOf(designId.toString()))
    }

    // ============== CLOTHING METHODS ==============
    fun insertClothingItem(color: String, category: String, imagePath: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CLOTHING_COLOR, color)
            put(COLUMN_CLOTHING_CATEGORY, category)
            put(COLUMN_CLOTHING_IMAGE, imagePath)
        }
        return try {
            db.insertOrThrow(CLOTHING_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting clothing item", e)
            -1L
        }
    }

    fun getAllClothingItems(): List<ClothingItem> {
        val items = mutableListOf<ClothingItem>()
        val db = readableDatabase

        db.query(CLOTHING_TABLE_NAME, null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_ID))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_COLOR))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_CATEGORY))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_IMAGE))
                items.add(ClothingItem(id, color, category, image))
            }
        }
        return items
    }

    fun deleteClothingItem(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            CLOTHING_TABLE_NAME,
            "$COLUMN_CLOTHING_ID = ?",
            arrayOf(id.toString())
        )
    }

    // ============== FAVORITES METHODS ==============
    fun addToFavorites(clothingId: Long, userEmail: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("clothing_id", clothingId)
            put("user_email", userEmail)
            put("favorited_at", System.currentTimeMillis())
        }
        return try {
            db.insertOrThrow("Favorites", null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding to favorites", e)
            -1L
        }
    }

    fun removeFromFavorites(clothingId: Long, userEmail: String): Int {
        val db = this.writableDatabase
        return db.delete(
            "Favorites",
            "clothing_id = ? AND user_email = ?",
            arrayOf(clothingId.toString(), userEmail)
        )
    }

    fun isItemFavorited(clothingId: Long, userEmail: String): Boolean {
        val db = this.readableDatabase
        return db.query(
            "Favorites",
            arrayOf("id"),
            "clothing_id = ? AND user_email = ?",
            arrayOf(clothingId.toString(), userEmail),
            null, null, null
        ).use { cursor ->
            cursor.count > 0
        }
    }

    fun getFavoriteItems(userEmail: String): List<ClothingItem> {
        val favoriteItems = mutableListOf<ClothingItem>()
        val db = this.readableDatabase
        val query = """
            SELECT c.$COLUMN_CLOTHING_ID, c.$COLUMN_CLOTHING_COLOR, c.$COLUMN_CLOTHING_CATEGORY, c.$COLUMN_CLOTHING_IMAGE
            FROM $CLOTHING_TABLE_NAME c
            JOIN Favorites f ON c.$COLUMN_CLOTHING_ID = f.clothing_id
            WHERE f.user_email = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(userEmail)).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_ID))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_COLOR))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_CATEGORY))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING_IMAGE))
                favoriteItems.add(ClothingItem(id, color, category, image))
            }
        }
        return favoriteItems
    }

    fun insertFavoriteOutfit(outfitDescription: String, clothingItemIds: List<Int>): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("description", outfitDescription)
            put("clothing_ids", clothingItemIds.joinToString(","))
        }
        return db.insert("favorite_outfits", null, values)
    }

    // ============== OUTFIT METHODS ==============
    fun insertOutfit(season: String, description: String, clothingItemIds: List<Long>): Long {
        val db = writableDatabase
        db.beginTransaction()
        var outfitId = -1L

        try {
            // Insert into Outfit table
            val outfitValues = ContentValues().apply {
                put("season", season)
                put("description", description)
            }
            outfitId = db.insert("Outfit", null, outfitValues)

            // Insert into OutfitItems table
            clothingItemIds.forEach { clothingId ->
                val itemValues = ContentValues().apply {
                    put("outfit_id", outfitId)
                    put("clothing_id", clothingId)
                }
                db.insert("OutfitItems", null, itemValues)
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting outfit", e)
        } finally {
            db.endTransaction()
        }

        return outfitId
    }
    data class Outfit(
        val id: Long,
        val clothingItems: List<ClothingItem>,
        val season: String,
        val description: String
    ) {
        fun joinToString(function: () -> String): Unit {

        }
    }

    fun getAllOutfits(): List<Outfit> {
        val db = readableDatabase
        val outfits = mutableListOf<Outfit>()

        db.query("Outfit", null, null, null, null, null, null).use { outfitCursor ->
            while (outfitCursor.moveToNext()) {
                val outfitId = outfitCursor.getLong(outfitCursor.getColumnIndexOrThrow("id"))
                val season = outfitCursor.getString(outfitCursor.getColumnIndexOrThrow("season"))
                val description = outfitCursor.getString(outfitCursor.getColumnIndexOrThrow("description"))

                // Get all clothing items for this outfit
                val clothingItems = mutableListOf<ClothingItem>()
                val clothingQuery = """
                    SELECT c.$COLUMN_CLOTHING_ID, c.$COLUMN_CLOTHING_COLOR, c.$COLUMN_CLOTHING_CATEGORY, c.$COLUMN_CLOTHING_IMAGE
                    FROM $CLOTHING_TABLE_NAME c
                    JOIN OutfitItems oi ON c.$COLUMN_CLOTHING_ID = oi.clothing_id
                    WHERE oi.outfit_id = ?
                """.trimIndent()

                db.rawQuery(clothingQuery, arrayOf(outfitId.toString())).use { clothingCursor ->
                    while (clothingCursor.moveToNext()) {
                        val id = clothingCursor.getLong(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_ID))
                        val color = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_COLOR))
                        val category = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_CATEGORY))
                        val image = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_IMAGE))
                        clothingItems.add(ClothingItem(id, color, category, image))
                    }
                }

                outfits.add(Outfit(outfitId, clothingItems, season, description))
            }
        }
        return outfits
    }

    fun deleteOutfit(outfitId: Long): Int {
        val db = writableDatabase
        return db.delete("Outfit", "id = ?", arrayOf(outfitId.toString()))
    }

    // ============== FAVORITE OUTFITS METHODS ==============
    fun addFavoriteOutfit(outfitId: Long, userEmail: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("outfit_id", outfitId)
            put("user_email", userEmail)
            put("favorited_at", System.currentTimeMillis())
        }
        return try {
            db.insertOrThrow("FavoriteOutfits", null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding favorite outfit", e)
            -1L
        }
    }

    fun removeFavoriteOutfit(outfitId: Long, userEmail: String): Int {
        val db = writableDatabase
        return db.delete(
            "FavoriteOutfits",
            "outfit_id = ? AND user_email = ?",
            arrayOf(outfitId.toString(), userEmail)
        )
    }

    fun isOutfitFavorited(outfitId: Long, userEmail: String): Boolean {
        val db = readableDatabase
        return db.query(
            "FavoriteOutfits",
            arrayOf("id"),
            "outfit_id = ? AND user_email = ?",
            arrayOf(outfitId.toString(), userEmail),
            null, null, null
        ).use { cursor ->
            cursor.count > 0
        }
    }

    fun getFavoriteOutfits(userEmail: String): List<Pair<Outfit, Long>> {
        val favorites = mutableListOf<Pair<Outfit, Long>>()
        val db = readableDatabase
        val query = """
            SELECT o.id, o.season, o.description, f.favorited_at
            FROM Outfit o
            JOIN FavoriteOutfits f ON o.id = f.outfit_id
            WHERE f.user_email = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(userEmail)).use { cursor ->
            while (cursor.moveToNext()) {
                val outfitId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val season = cursor.getString(cursor.getColumnIndexOrThrow("season"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val favoritedAt = cursor.getLong(cursor.getColumnIndexOrThrow("favorited_at"))

                // Get clothing items for the outfit
                val clothingItems = mutableListOf<ClothingItem>()
                val clothingQuery = """
                    SELECT c.* FROM $CLOTHING_TABLE_NAME c
                    JOIN OutfitItems oi ON c.$COLUMN_CLOTHING_ID = oi.clothing_id
                    WHERE oi.outfit_id = ?
                """.trimIndent()

                db.rawQuery(clothingQuery, arrayOf(outfitId.toString())).use { clothingCursor ->
                    while (clothingCursor.moveToNext()) {
                        val id = clothingCursor.getLong(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_ID))
                        val color = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_COLOR))
                        val category = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_CATEGORY))
                        val image = clothingCursor.getString(clothingCursor.getColumnIndexOrThrow(COLUMN_CLOTHING_IMAGE))
                        clothingItems.add(ClothingItem(id, color, category, image))
                    }
                }

                favorites.add(Pair(Outfit(outfitId, clothingItems, season, description), favoritedAt))
            }
        }
        return favorites
    }
    fun deleteUserCompletely(userId: Long, userEmail: String): Boolean {
        val db = writableDatabase
        var success = false
        db.beginTransaction()
        try {
            // Delete from Favorites where user_email matches
            db.delete(
                "Favorites",
                "user_email = ?",
                arrayOf(userEmail)
            )
            // Delete from FavoriteOutfits where user_email matches
            db.delete(
                "FavoriteOutfits",
                "user_email = ?",
                arrayOf(userEmail)
            )
            // Delete user from Enthusiast table by ID
            val deletedRows = db.delete(
                TABLE_NAME,
                "$COLUMN_ID = ?",
                arrayOf(userId.toString())
            )

            // If user deletion affected 1 row, consider successful
            success = deletedRows == 1

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting user completely", e)
        } finally {
            db.endTransaction()
        }
        return success
    }


    fun getUserIdByEmail(email: String): Long? {
        val normalizedEmail = email.trim().lowercase()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ID FROM $TABLE_NAME WHERE TRIM(LOWER($COLUMN_EMAIL)) = ?",
            arrayOf(normalizedEmail)
        )
        cursor.use {
            return if (it.moveToFirst()) {
                it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
            } else {
                Log.e("DatabaseHelper", "No user found with email: $normalizedEmail")
                null
            }
        }
    }
    fun changeUserPassword(email: String, oldPassword: String, newPassword: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_PASSWORD FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?",
            arrayOf(email)
        )

        cursor.use {
            if (it.moveToFirst()) {
                val currentPassword = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD))
                if (currentPassword == oldPassword) {
                    val values = ContentValues().apply {
                        put(COLUMN_PASSWORD, newPassword)
                    }
                    db.update(TABLE_NAME, values, "$COLUMN_EMAIL = ?", arrayOf(email))
                    return true
                }
            }
        }
        return false
    }
}