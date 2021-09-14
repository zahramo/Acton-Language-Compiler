.class public Carwash
.super Actor

.field clean_factor I

.method public <init>(I)V
.limit stack 16
.limit locals 16
aload_0
ldc 0
putfield Carwash/clean_factor I
aload_0
iload_1
invokespecial Actor/<init>(I)V
return
.end method

.method public initial(I)V
.limit stack 16
.limit locals 16
aload_0
iload 1
putfield Carwash/clean_factor I
return
.end method

.method public setKnownActors()V
.limit stack 16
.limit locals 16
return
.end method

.method public send_wash(LActor;II)V
.limit stack 16
.limit locals 16
aload_0
new Carwash_wash
dup
aload_0
aload_1

invokespecial Carwash_wash/<init>(LCarwash;LActor;II)V
invokevirtual Carwash/send(LMessage;)V
return
.end method

.method public wash(LActor;II)V
.limit stack 16
.limit locals 16
Label0:
iload 2
iload 3
if_icmple Label2
iconst_1
goto Label3
Label2:
iconst_0
Label3:
ifeq Label1
iload 3
aload_0 
getfield Carwash/clean_factor I
iadd
istore 3
goto Label0
Label1:
aload_1
aload_0
invokevirtual Actor/send_ready(LActor;)V
return
.end method

