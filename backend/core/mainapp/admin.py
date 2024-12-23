from django.contrib import admin
from .models import Post, Comment, Hashtag, PostHashtag

admin.site.register(Post)
admin.site.register(Comment)
admin.site.register(Hashtag)
admin.site.register(PostHashtag)