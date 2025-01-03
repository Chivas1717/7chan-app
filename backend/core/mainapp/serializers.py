# mainapp/serializers.py

from rest_framework import serializers
from .models import Post, Comment, Hashtag, PostHashtag, User



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email']


class CommentSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)
    class Meta:
        model = Comment
        fields = ['id', 'author', 'content', 'created_at']
        read_only_fields = ['author', 'created_at']

    def create(self, validated_data):
        request = self.context.get('request')
        if request and hasattr(request, 'user'):
            # Встановлюємо автора як поточного користувача
            validated_data['author'] = request.user
        return super().create(validated_data)


class PostSerializer(serializers.ModelSerializer):
    hashtags = serializers.ListField(
        child=serializers.CharField(max_length=100),
        write_only=True,
        required=False
    )
    hashtag_list = serializers.SerializerMethodField(read_only=True)
    comments = CommentSerializer(many=True, read_only=True)
    author = UserSerializer(read_only=True)

    class Meta:
        model = Post
        fields = ['id', 'title', 'content', 'created_at', 'author',
                  'hashtags', 'hashtag_list', 'comments']
        read_only_fields = ['author', 'created_at']

    def get_hashtag_list(self, obj):
        return [ph.hashtag.name for ph in obj.posthashtags.all()]

    def create(self, validated_data):
        hashtags_data = validated_data.pop('hashtags', [])
        post = Post.objects.create(**validated_data)

        for tag_name in hashtags_data:
            tag_name = tag_name.lower().strip()
            hashtag, created = Hashtag.objects.get_or_create(name=tag_name)
            # Створюємо запис в PostHashtag
            PostHashtag.objects.create(post=post, hashtag=hashtag)

        return post

        return post

    def update(self, instance, validated_data):
        # Оновлюємо поля
        hashtags_data = validated_data.pop('hashtags', [])
        instance.title = validated_data.get('title', instance.title)
        instance.content = validated_data.get('content', instance.content)
        instance.save()

        # Якщо прийшли нові хештеги — оновимо
        if hashtags_data:
            instance.hashtag_set.clear()
            for tag_name in hashtags_data:
                tag_name = tag_name.lower().strip()
                hashtag, created = Hashtag.objects.get_or_create(name=tag_name)
                instance.hashtag_set.add(hashtag)

        return instance


class PostListSerializer(serializers.ModelSerializer):
    hashtag_list = serializers.SerializerMethodField()
    comments = CommentSerializer(many=True, read_only=True)
    author = UserSerializer(read_only=True)  # Додаємо UserSerializer

    class Meta:
        model = Post
        fields = ['id', 'title', 'content', 'created_at', 'hashtag_list', 'comments', 'author']

    def get_hashtag_list(self, obj):
        return [ph.hashtag.name for ph in obj.posthashtags.all()]


class PostDetailSerializer(serializers.ModelSerializer):
    comments = CommentSerializer(many=True, read_only=True)
    hashtag_list = serializers.SerializerMethodField()
    author = UserSerializer(read_only=True)

    class Meta:
        model = Post
        fields = ['id', 'author', 'title', 'content', 'created_at', 'comments', 'hashtag_list']

    def get_hashtag_list(self, obj):
        return [ph.hashtag.name for ph in obj.posthashtags.all()]


class HashtagSerializer(serializers.ModelSerializer):
    class Meta:
        model = Hashtag
        fields = ['id', 'name']


class PostHashtagSerializer(serializers.ModelSerializer):
    class Meta:
        model = PostHashtag
        fields = '__all__'


class UserProfileSerializer(serializers.ModelSerializer):
    posts = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ('id', 'username', 'email', 'posts')

    def get_posts(self, obj):
        # Витягуємо всі пости автора (obj — це User)
        user_posts = Post.objects.filter(author=obj)
        # Якщо є вже PostSerializer, можемо його використати
        # Але можна і просто показати id/title
        return PostListSerializer(user_posts, many=True).data


class RegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, min_length=6)

    class Meta:
        model = User
        fields = ('username', 'email', 'password')

    def create(self, validated_data):
        # Витягуємо пароль окремо
        password = validated_data.pop('password')
        # Створюємо користувача
        user = User(**validated_data)
        user.set_password(password)  # хешує пароль
        user.save()
        return user
