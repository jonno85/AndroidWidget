/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\users\\F31999A\\workspace\\FirstWidget\\aidl\\com\\example\\widgetservice\\IBoundService.aidl
 */
package com.example.widgetservice;
public interface IBoundService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.widgetservice.IBoundService
{
private static final java.lang.String DESCRIPTOR = "com.example.widgetservice.IBoundService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.widgetservice.IBoundService interface,
 * generating a proxy if needed.
 */
public static com.example.widgetservice.IBoundService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.widgetservice.IBoundService))) {
return ((com.example.widgetservice.IBoundService)iin);
}
return new com.example.widgetservice.IBoundService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_putSeed:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.putSeed(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getValue:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getValue();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setServiceRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setServiceRunning(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentResult:
{
data.enforceInterface(DESCRIPTOR);
com.example.widgetservice.GeneratedValueResult _result = this.getCurrentResult();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addListener:
{
data.enforceInterface(DESCRIPTOR);
com.example.widgetservice.IGeneratedValueListener _arg0;
_arg0 = com.example.widgetservice.IGeneratedValueListener.Stub.asInterface(data.readStrongBinder());
this.addListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
com.example.widgetservice.IGeneratedValueListener _arg0;
_arg0 = com.example.widgetservice.IGeneratedValueListener.Stub.asInterface(data.readStrongBinder());
this.removeListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.widgetservice.IBoundService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void putSeed(int seed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(seed);
mRemote.transact(Stub.TRANSACTION_putSeed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getValue() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getValue, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setServiceRunning(boolean value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((value)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setServiceRunning, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public com.example.widgetservice.GeneratedValueResult getCurrentResult() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.example.widgetservice.GeneratedValueResult _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentResult, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.example.widgetservice.GeneratedValueResult.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addListener(com.example.widgetservice.IGeneratedValueListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListener(com.example.widgetservice.IGeneratedValueListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_putSeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setServiceRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getCurrentResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_addListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void putSeed(int seed) throws android.os.RemoteException;
public int getValue() throws android.os.RemoteException;
public void setServiceRunning(boolean value) throws android.os.RemoteException;
public com.example.widgetservice.GeneratedValueResult getCurrentResult() throws android.os.RemoteException;
public void addListener(com.example.widgetservice.IGeneratedValueListener listener) throws android.os.RemoteException;
public void removeListener(com.example.widgetservice.IGeneratedValueListener listener) throws android.os.RemoteException;
}
