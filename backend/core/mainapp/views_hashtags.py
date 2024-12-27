from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from .models import Hashtag
from .serializers import HashtagSerializer

class HashtagViewSet(viewsets.ModelViewSet):
    """
    ReadOnlyModelViewSet:
      - GET list ( /hashtags/ )
      - GET detail ( /hashtags/{id}/ )
    """
    queryset = Hashtag.objects.all()
    serializer_class = HashtagSerializer
    permission_classes = [AllowAny]  # чи лише для авторизованих