from django.test import TestCase


from django.test import TestCase
from django.urls import reverse
from rest_framework import status


class PhotoBlogAPITests(TestCase):

    def test_post_list_status(self):
        response = self.client.get("/api_root/Post/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_post_list_json(self):
        response = self.client.get("/api_root/Post/")
        self.assertEqual(response["Content-Type"], "application/json")

    def test_post_list_is_empty_initially(self):
        response = self.client.get("/api_root/Post/")
        self.assertEqual(response.json(), [])
