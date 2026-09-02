package com.unsent.messenger.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  private final EntityDeletionOrUpdateAdapter<MessageEntity> __updateAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsUnsent;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsUnsentByText;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConversationMessages;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessageById;

  private final SharedSQLiteStatement __preparedStmtOfClearAllMessages;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `messages` (`id`,`conversationId`,`conversationTitle`,`senderName`,`messageText`,`timestamp`,`isUnsent`,`packageName`,`mediaFilePath`,`mediaType`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getConversationId());
        statement.bindString(3, entity.getConversationTitle());
        statement.bindString(4, entity.getSenderName());
        statement.bindString(5, entity.getMessageText());
        statement.bindLong(6, entity.getTimestamp());
        final int _tmp = entity.isUnsent() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindString(8, entity.getPackageName());
        if (entity.getMediaFilePath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getMediaFilePath());
        }
        if (entity.getMediaType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getMediaType());
        }
      }
    };
    this.__updateAdapterOfMessageEntity = new EntityDeletionOrUpdateAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `messages` SET `id` = ?,`conversationId` = ?,`conversationTitle` = ?,`senderName` = ?,`messageText` = ?,`timestamp` = ?,`isUnsent` = ?,`packageName` = ?,`mediaFilePath` = ?,`mediaType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getConversationId());
        statement.bindString(3, entity.getConversationTitle());
        statement.bindString(4, entity.getSenderName());
        statement.bindString(5, entity.getMessageText());
        statement.bindLong(6, entity.getTimestamp());
        final int _tmp = entity.isUnsent() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindString(8, entity.getPackageName());
        if (entity.getMediaFilePath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getMediaFilePath());
        }
        if (entity.getMediaType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getMediaType());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfMarkAsUnsent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE messages SET isUnsent = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsUnsentByText = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE messages SET isUnsent = 1 WHERE conversationId = ? AND messageText = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConversationMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE conversationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMessageById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final MessageEntity message,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMessageEntity.insertAndReturnId(message);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsUnsent(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsUnsent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfMarkAsUnsent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsUnsentByText(final String conversationId, final String messageText,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsUnsentByText.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, conversationId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, messageText);
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
          __preparedStmtOfMarkAsUnsentByText.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConversationMessages(final String conversationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConversationMessages.acquire();
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
          __preparedStmtOfDeleteConversationMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessageById(final long messageId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessageById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, messageId);
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
          __preparedStmtOfDeleteMessageById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllMessages(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllMessages.acquire();
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
          __preparedStmtOfClearAllMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MessageEntity>> getMessagesForConversation(final String conversationId) {
    final String _sql = "SELECT * FROM messages WHERE conversationId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfConversationTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationTitle");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsUnsent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnsent");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfMediaFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaFilePath");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpConversationTitle;
            _tmpConversationTitle = _cursor.getString(_cursorIndexOfConversationTitle);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpIsUnsent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnsent);
            _tmpIsUnsent = _tmp != 0;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpMediaFilePath;
            if (_cursor.isNull(_cursorIndexOfMediaFilePath)) {
              _tmpMediaFilePath = null;
            } else {
              _tmpMediaFilePath = _cursor.getString(_cursorIndexOfMediaFilePath);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpConversationTitle,_tmpSenderName,_tmpMessageText,_tmpTimestamp,_tmpIsUnsent,_tmpPackageName,_tmpMediaFilePath,_tmpMediaType);
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
  public Object getLatestMessage(final String conversationId,
      final Continuation<? super MessageEntity> $completion) {
    final String _sql = "SELECT * FROM messages WHERE conversationId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MessageEntity>() {
      @Override
      @Nullable
      public MessageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfConversationTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationTitle");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsUnsent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnsent");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfMediaFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaFilePath");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final MessageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpConversationTitle;
            _tmpConversationTitle = _cursor.getString(_cursorIndexOfConversationTitle);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpIsUnsent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnsent);
            _tmpIsUnsent = _tmp != 0;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpMediaFilePath;
            if (_cursor.isNull(_cursorIndexOfMediaFilePath)) {
              _tmpMediaFilePath = null;
            } else {
              _tmpMediaFilePath = _cursor.getString(_cursorIndexOfMediaFilePath);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            _result = new MessageEntity(_tmpId,_tmpConversationId,_tmpConversationTitle,_tmpSenderName,_tmpMessageText,_tmpTimestamp,_tmpIsUnsent,_tmpPackageName,_tmpMediaFilePath,_tmpMediaType);
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
  public Flow<List<MessageEntity>> getUnsentMessagesForConversation(final String conversationId) {
    final String _sql = "SELECT * FROM messages WHERE conversationId = ? AND isUnsent = 1 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfConversationTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationTitle");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsUnsent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnsent");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfMediaFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaFilePath");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpConversationTitle;
            _tmpConversationTitle = _cursor.getString(_cursorIndexOfConversationTitle);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpIsUnsent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnsent);
            _tmpIsUnsent = _tmp != 0;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpMediaFilePath;
            if (_cursor.isNull(_cursorIndexOfMediaFilePath)) {
              _tmpMediaFilePath = null;
            } else {
              _tmpMediaFilePath = _cursor.getString(_cursorIndexOfMediaFilePath);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpConversationTitle,_tmpSenderName,_tmpMessageText,_tmpTimestamp,_tmpIsUnsent,_tmpPackageName,_tmpMediaFilePath,_tmpMediaType);
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
  public Object countUnsentMessages(final String conversationId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM messages WHERE conversationId = ? AND isUnsent = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object countTotalMessages(final String conversationId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM messages WHERE conversationId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Flow<List<MessageEntity>> searchMessages(final String query) {
    final String _sql = "SELECT * FROM messages WHERE messageText LIKE '%' || ? || '%' OR senderName LIKE '%' || ? || '%' ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfConversationTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationTitle");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsUnsent = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnsent");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfMediaFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaFilePath");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpConversationTitle;
            _tmpConversationTitle = _cursor.getString(_cursorIndexOfConversationTitle);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpIsUnsent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnsent);
            _tmpIsUnsent = _tmp != 0;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpMediaFilePath;
            if (_cursor.isNull(_cursorIndexOfMediaFilePath)) {
              _tmpMediaFilePath = null;
            } else {
              _tmpMediaFilePath = _cursor.getString(_cursorIndexOfMediaFilePath);
            }
            final String _tmpMediaType;
            if (_cursor.isNull(_cursorIndexOfMediaType)) {
              _tmpMediaType = null;
            } else {
              _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpConversationTitle,_tmpSenderName,_tmpMessageText,_tmpTimestamp,_tmpIsUnsent,_tmpPackageName,_tmpMediaFilePath,_tmpMediaType);
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
