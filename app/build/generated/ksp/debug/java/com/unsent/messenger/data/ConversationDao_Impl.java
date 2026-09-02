package com.unsent.messenger.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ConversationDao_Impl implements ConversationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConversationEntity> __insertionAdapterOfConversationEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCounts;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConversation;

  private final SharedSQLiteStatement __preparedStmtOfClearAllConversations;

  public ConversationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConversationEntity = new EntityInsertionAdapter<ConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `conversations` (`conversationId`,`title`,`lastMessage`,`lastSender`,`lastTimestamp`,`unsentCount`,`totalMessagesCount`,`packageName`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConversationEntity entity) {
        statement.bindString(1, entity.getConversationId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getLastMessage());
        statement.bindString(4, entity.getLastSender());
        statement.bindLong(5, entity.getLastTimestamp());
        statement.bindLong(6, entity.getUnsentCount());
        statement.bindLong(7, entity.getTotalMessagesCount());
        statement.bindString(8, entity.getPackageName());
      }
    };
    this.__preparedStmtOfUpdateCounts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE conversations SET unsentCount = ?, totalMessagesCount = ? WHERE conversationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConversation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM conversations WHERE conversationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllConversations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM conversations";
        return _query;
      }
    };
  }

  @Override
  public Object upsertConversation(final ConversationEntity conversation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfConversationEntity.insert(conversation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCounts(final String conversationId, final int unsentCount,
      final int totalCount, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCounts.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, unsentCount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, totalCount);
        _argIndex = 3;
        _stmt.bindString(_argIndex, conversationId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateCounts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConversation(final String conversationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConversation.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, conversationId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteConversation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllConversations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllConversations.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllConversations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ConversationEntity>> getAllConversations() {
    final String _sql = "SELECT * FROM conversations ORDER BY lastTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"conversations"}, new Callable<List<ConversationEntity>>() {
      @Override
      @NonNull
      public List<ConversationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastSender = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSender");
          final int _cursorIndexOfLastTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTimestamp");
          final int _cursorIndexOfUnsentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unsentCount");
          final int _cursorIndexOfTotalMessagesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMessagesCount");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final List<ConversationEntity> _result = new ArrayList<ConversationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConversationEntity _item;
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpLastMessage;
            _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            final String _tmpLastSender;
            _tmpLastSender = _cursor.getString(_cursorIndexOfLastSender);
            final long _tmpLastTimestamp;
            _tmpLastTimestamp = _cursor.getLong(_cursorIndexOfLastTimestamp);
            final int _tmpUnsentCount;
            _tmpUnsentCount = _cursor.getInt(_cursorIndexOfUnsentCount);
            final int _tmpTotalMessagesCount;
            _tmpTotalMessagesCount = _cursor.getInt(_cursorIndexOfTotalMessagesCount);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            _item = new ConversationEntity(_tmpConversationId,_tmpTitle,_tmpLastMessage,_tmpLastSender,_tmpLastTimestamp,_tmpUnsentCount,_tmpTotalMessagesCount,_tmpPackageName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getConversationById(final String conversationId,
      final Continuation<? super ConversationEntity> $completion) {
    final String _sql = "SELECT * FROM conversations WHERE conversationId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ConversationEntity>() {
      @Override
      @Nullable
      public ConversationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastSender = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSender");
          final int _cursorIndexOfLastTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTimestamp");
          final int _cursorIndexOfUnsentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unsentCount");
          final int _cursorIndexOfTotalMessagesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMessagesCount");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final ConversationEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpLastMessage;
            _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            final String _tmpLastSender;
            _tmpLastSender = _cursor.getString(_cursorIndexOfLastSender);
            final long _tmpLastTimestamp;
            _tmpLastTimestamp = _cursor.getLong(_cursorIndexOfLastTimestamp);
            final int _tmpUnsentCount;
            _tmpUnsentCount = _cursor.getInt(_cursorIndexOfUnsentCount);
            final int _tmpTotalMessagesCount;
            _tmpTotalMessagesCount = _cursor.getInt(_cursorIndexOfTotalMessagesCount);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            _result = new ConversationEntity(_tmpConversationId,_tmpTitle,_tmpLastMessage,_tmpLastSender,_tmpLastTimestamp,_tmpUnsentCount,_tmpTotalMessagesCount,_tmpPackageName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ConversationEntity>> searchConversations(final String query) {
    final String _sql = "SELECT * FROM conversations WHERE title LIKE '%' || ? || '%' OR lastMessage LIKE '%' || ? || '%' ORDER BY lastTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"conversations"}, new Callable<List<ConversationEntity>>() {
      @Override
      @NonNull
      public List<ConversationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastSender = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSender");
          final int _cursorIndexOfLastTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTimestamp");
          final int _cursorIndexOfUnsentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unsentCount");
          final int _cursorIndexOfTotalMessagesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMessagesCount");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final List<ConversationEntity> _result = new ArrayList<ConversationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConversationEntity _item;
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpLastMessage;
            _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            final String _tmpLastSender;
            _tmpLastSender = _cursor.getString(_cursorIndexOfLastSender);
            final long _tmpLastTimestamp;
            _tmpLastTimestamp = _cursor.getLong(_cursorIndexOfLastTimestamp);
            final int _tmpUnsentCount;
            _tmpUnsentCount = _cursor.getInt(_cursorIndexOfUnsentCount);
            final int _tmpTotalMessagesCount;
            _tmpTotalMessagesCount = _cursor.getInt(_cursorIndexOfTotalMessagesCount);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            _item = new ConversationEntity(_tmpConversationId,_tmpTitle,_tmpLastMessage,_tmpLastSender,_tmpLastTimestamp,_tmpUnsentCount,_tmpTotalMessagesCount,_tmpPackageName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
