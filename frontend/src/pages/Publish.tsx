import { useEffect, useState } from "react";
import { Appbar } from "../components/Appbar";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { BACKEND_URL, BACKEND_URL_BLOG } from "../config";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { storage } from "../firebaseConfig";
import { v4 } from "uuid";
import { Loading } from "../components/Loading";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "../quill.css";
import { z } from "zod";
import { toast } from "react-toastify";

const MAX_FILE_SIZE = 3 * 1024 * 1024;
const ALLOWED_EXTENSIONS = ["jpg", "jpeg", "png"];

const fileSchema = z.object({
  name: z.string(),
  size: z.number().max(MAX_FILE_SIZE, "File size must be less than 2MB"),
  type: z.string().refine(
    (type) => {
      const extension = type.split("/").pop();
      return (
        typeof extension === "string" && ALLOWED_EXTENSIONS.includes(extension)
      );
    },
    {
      message: "File type must be one of jpg, jpeg, or png",
    }
  ),
});

export const Publish = () => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [image, setImage] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [premium,setPremium] = useState(false);
  const navigate = useNavigate();

  useEffect(()=>{
    if(localStorage.getItem("token")==null)
      navigate("/signin");
    if(localStorage.getItem("premium")!=null&&localStorage.getItem("premium")=="true")
    {
      setPremium(true);
    }
    else if(localStorage.getItem("premium")!=null&&localStorage.getItem("premium")!="true")
      {
        setPremium(false);
      }

    // console.log(localStorage.getItem("token"));
    axios.post(`${BACKEND_URL}/user/isSubscribed`, null, {
      headers: {
        Authorization: `${localStorage.getItem("token")}`, // Assuming Bearer token is used
      },
    })
      .then((response) => {
        if (response.status === 200) {
          localStorage.setItem("premium", "true");
          setPremium(true);
          console.log(premium);
        } else {
          localStorage.setItem("premium", "false");
          setPremium(false);
        }
      })
      .catch((err) => {
        console.error("Error in API call:", err);
        localStorage.setItem("premium", "false");
        setPremium(false);
      });
    
  },[])

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setImage(e.target.files[0]);
    }
  };

  function getRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  const handleClick = async () => {

    if(title==null||title.length==0){
      toast.error("Title cannot be empty");
      return ;
    }
    console.log(import.meta.env.VITE_storageBucket);
    const num = getRandomNumber(1, 50);
    setLoading(true);
    let imageUrl = `https://picsum.photos/300/300?${num}`;

    if (image) {
      const validationResult = fileSchema.safeParse(image);
      if (!validationResult.success) {
        if (validationResult.error.errors.length > 1) {
          toast.error(validationResult.error.errors[0].message);
          toast.error(validationResult.error.errors[1].message);
        } else {
          toast.error(validationResult.error.errors[0].message);
        }
        setLoading(false);
        return;
      }

      const imageRef = ref(storage, `images/${v4()}`);
      try {
        await uploadBytes(imageRef, image);
        imageUrl = await getDownloadURL(imageRef);
      } catch (error) {
        console.error("Error uploading image:", error);
      }
    }

    try {
      const response = await axios.post(
        `${BACKEND_URL_BLOG}/blog/create/new-blog`,
        {
          title,
          content: description,
          imgUrl: imageUrl,
        },
        {
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        }
      );

      navigate(`/blog/${response.data.id}`);
    } catch (error) {
      console.error("Error posting blog:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateClick = async () => {
    if (!title) {
      console.log("Please enter a title");
      return;
    }
    setGenerating(true);
    try {
      const response = await axios.post(
        `${BACKEND_URL}/genAI`,
        { title },
        {
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        }
      );
      console.log(response);
      setDescription(response.data.generated_text);
    } catch (error) {
      console.error("Error generating article:", error);
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div>
      <Appbar />
      <div className="flex justify-center w-full pt-8 p-5">
        <div className="max-w-screen-lg w-full">
          <input
            onChange={(e) => {
              setTitle(e.target.value);
            }}
            type="text"
            className="bg-gray-50 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            placeholder="Title"
          />

          <div className="flex justify-between mt-2"></div>

          <TextEditor
            onChange={setDescription}
            value={description} // Pass description value to TextEditor
          />

          <button
            onClick={handleClick}
            type="submit"
            className="mt-4 inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-blue-200 dark:focus:ring-blue-900 hover:bg-blue-800"
            disabled={loading} // Disable button when loading
          >
            {loading ? "Publishing..." : "Publish post"}
          </button>

          <button
            onClick={handleGenerateClick}
            type="button"
            className={`ml-4 inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white ${premium ? 'bg-green-700 hover:bg-green-800 focus:ring-green-200' : 'bg-green-300 hover:bg-green-400 focus:ring-green-200'} rounded-lg focus:ring-4 dark:focus:ring-green-900`}
            disabled={generating || !premium} 
            style={{ cursor: !premium ? 'not-allowed' : 'pointer' }}
          >
            {generating ? "Generating..." : "Generate with AI"}
          </button>
          <input className="ml-4" type="file" onChange={handleImageChange} />

          {loading && (
            <div className="mt-4 flex justify-center">
              <Loading />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export function TextEditor({
  onChange,
  value,
}: {
  onChange: (value: string) => void;
  value: string;
}) {
  var toolbarOptions = [
    ["bold", "italic", "underline", "strike"],
    ["blockquote", "code-block"],
    ["link", "image", "formula"],

    [{ header: 1 }, { header: 2 }],
    [{ list: "ordered" }, { list: "bullet" }, { list: "check" }],
    [{ indent: "-1" }, { indent: "+1" }],
    [{ direction: "rtl" }], // text direction

    [{ size: ["small", false, "large", "huge"] }], // custom dropdown
    [{ header: [1, 2, 3, 4, 5, 6, false] }],

    [{ color: [] }, { background: [] }], // dropdown with defaults from theme
    [{ font: [] }],
    [{ align: [] }],

    ["clean"],
  ];
  const module = {
    toolbar: toolbarOptions,
  };
  return (
    <div className="mt-2">
      <div className="w-full mb-4] ">
        <div className="flex items-center justify-between border">
          <div className="my-2 bg-white rounded-b-lg w-full">
            <label className="sr-only">Publish post</label>
            <ReactQuill
              modules={module}
              theme="snow"
              value={value}
              onChange={onChange}
              className="custom-quill"
              placeholder="Write an article..."
            />
          </div>
        </div>
      </div>
    </div>
  );
}
